package com.example.mcdonalds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardMultilineWidget;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView tvTotal, tvDate, tvTable;
    Button payButton2;
    RadioButton rdiCard, rdiCash;
    Toolbar toolbar;
    String storeId = "", orderId = "";
    RecyclerView recycler_cart;
    String status = "";
    String checkout = "";
    boolean cash = false;
    CardMultilineWidget cardInputWidget2;

    //*************** Stripe
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = Common.API_RESTAURANT_ENDPOINT;

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    //*************** Stripe

    IMcDonaldsAPI iMcDonaldsAPI;
    BackgroundSliderAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    boolean isSelectDate = false, isAddNewAddr = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTable = (TextView) findViewById(R.id.tvTable);
        payButton2 = (Button) findViewById(R.id.payButton2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rdiCard = (RadioButton) findViewById(R.id.rdiCard);
        rdiCash = (RadioButton) findViewById(R.id.rdiCash);
        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
        cardInputWidget2 = (CardMultilineWidget) findViewById(R.id.cardInputWidget2);

        init();
        initView();

        //******** Stripe
        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51JZGEzFvYniupeojzk9qQNrYVbstkKXG5I4oY2HQj3RYROZP2eSiMHJbz31FFYrnrm2BFtuN2ZRCx2LmNM76VFwy00SYIfxeB4")
        );
        startCheckout();
        //******** Stripe

        rdiCard.setOnClickListener(view -> {
            if (rdiCard.isChecked()) {
                cardInputWidget2.setVisibility(View.VISIBLE);
            } else {
                cardInputWidget2.setVisibility(View.INVISIBLE);
            }
        });

        rdiCash.setOnClickListener(view -> {
            if (rdiCash.isChecked()) {
                cardInputWidget2.setVisibility(View.INVISIBLE);
            } else {
                cardInputWidget2.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getOrderNumer(boolean isOnlinePayment) {
        if (isOnlinePayment == true) {
            status = "WAITING";
            checkout = "N";
            cash = false;
        } else {
            status = "TODO";
            checkout = "N";
            cash = true;
        }
        orderId = UUID.randomUUID().toString();

        String address = "";
        compositeDisposable.add(cartDataSource.getAllCart2(Common.currentUser.getUserPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    compositeDisposable.add(iMcDonaldsAPI.createOrder(Common.API_KEY, orderId, Common.Imei, Common.currentUser.getUserPhone(), storeId, cash, Common.totalCash, status, checkout, address)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(createOrderModel -> {
                                if (createOrderModel.isSuccess()) {
                                    compositeDisposable.add(iMcDonaldsAPI.createOrderDetail(Common.API_KEY, orderId, new Gson().toJson(cartItems))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(updateOrderModel -> {
                                                if (updateOrderModel.isSuccess()) {
                                                    cartDataSource.cleanCart(Common.currentUser.getUserPhone())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new SingleObserver<Integer>() {
                                                                @Override
                                                                public void onSubscribe(@NonNull Disposable d) {

                                                                }

                                                                @Override
                                                                public void onSuccess(@NonNull Integer integer) {
//                                                                    displayAlert(
//                                                                            "Thông báo",
//                                                                            "Thanh toán thành công.");
//                                                                    clearAllItemInCart();
//                                                                        Intent homeActivity = new Intent(DonHangActivity.this, HomeActivity.class);
//                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                                        startActivity(homeActivity);

                                                                }

                                                                @Override
                                                                public void onError(@NonNull Throwable e) {
                                                                    Toast.makeText(OrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                            }, throwable -> {
                                                Toast.makeText(this, "[UPDATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                } else {
                                    Toast.makeText(this, "[CREATE ORDER]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                //Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }));
                }, throwable -> {
                    Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void clearAllItemInCart() {
        compositeDisposable.add(cartDataSource.getAllCart2(Common.currentUser.getUserPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    if (cartItems.isEmpty()) {
                        recycler_cart.setAdapter(null);
                    }
                }, throwable -> {
                    Toast.makeText(this, "[GET CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));
    }

    //******** Stripe
    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Float amount = Float.valueOf(tvTotal.getText().toString());
        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", Common.currency);
        payMap.put("amount", amount);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new OrderActivity.PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton2);

        payButton.setOnClickListener((View view) -> {

            if (rdiCash.isChecked()) {
                getOrderNumer(false);
//                Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                displayAlert(
                        "Thông báo",
                        "Thanh toán thành công.");
            } else {
                try {
                    getOrderNumer(true);
                    CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget2);
                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(this, confirmParams);
                        displayAlert(
                                "Thông báo",
                                "Thanh toán thành công.");
                    }
                } catch (Exception e) {
//                    Toast.makeText(this, "Thanh toán thành công.!", Toast.LENGTH_SHORT).show();
                    displayAlert(
                            "Thông báo",
                            "Thanh toán thành công.");
                    //cleanCard();
                    //finish();
                    //finish();
                }
            }
        });
    }
    //******** Stripe

    private void cleanCard() {
        cartDataSource.cleanCart(Common.currentUser.getUserPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
//                        displayAlert(
//                                "Thông báo",
//                                "Thanh toán thành công.");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(OrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new OrderActivity.PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<OrderActivity> activityRef;

        PayCallback(@NonNull OrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

//            activity.runOnUiThread(() ->
//                    Toast.makeText(
//                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
//                    ).show()
//            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<OrderActivity> activityRef;

        PaymentResultCallback(@NonNull OrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent)
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final OrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }

    private void initView() {
        ButterKnife.bind(this);

        tvTable.setText(Common.currentUser.getName());
        tvTotal.setText(Common.totalCash.toString());

        tvTable.setEnabled(false);
        tvTotal.setEnabled(false);
        cardInputWidget2.setVisibility(View.INVISIBLE);
        rdiCash.isChecked();
        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(OrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

//        payButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSelectDate) {
//                    Toast.makeText(OrderActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        });
    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectDate = true;
        tvDate.setText(new StringBuilder("")
                .append(monthOfYear + 1)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));
    }

}