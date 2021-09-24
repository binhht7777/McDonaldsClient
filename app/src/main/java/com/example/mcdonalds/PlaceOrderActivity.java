package com.example.mcdonalds;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
import com.example.mcdonalds.Adapter.MyCartAdapter;
import com.example.mcdonalds.Adapter.StoreAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDAO;
import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.CartItem;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.EventBus.SendTotalCashEvent;
import com.example.mcdonalds.EventBus.StoreLoadEvent;
import com.example.mcdonalds.Model.CreateOrderModel;
import com.example.mcdonalds.Model.Store;
import com.example.mcdonalds.Model.UpdateOrderModel;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.example.mcdonalds.Services.PicassoImageLoadingService;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.DialogInterface;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView edt_date;
    TextInputLayout outlinedTextField3;
    TextView txt_total_cash, txt_user_phone, txt_user_address, txt_new_address, text_name;
    Button btn_add_new_address, payButton;
    CheckBox ckb_default_address;
    RadioButton rdi_ood, rdi_online_payment;
    Toolbar toolbar;
    AutoCompleteTextView cmb_cuahang;
    List<Store> storeList;
    String storeId = "", orderId = "";
    RecyclerView recycler_store;

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
        setContentView(R.layout.activity_place_order);
        edt_date = (TextView) findViewById(R.id.edt_date);
//        outlinedTextField3 = (TextInputLayout) findViewById(R.id.outlinedTextField3);
        txt_total_cash = (TextView) findViewById(R.id.txt_total_cash);
        txt_user_phone = (TextView) findViewById(R.id.text_user_phone);
        txt_user_address = (TextView) findViewById(R.id.txt_user_address);
        //txt_new_address = (TextView) findViewById(R.id.txt_new_address);
        //btn_add_new_address = (Button) findViewById(R.id.btn_new_address);
        //ckb_default_address = (CheckBox) findViewById(R.id.chk_default_address);
        //rdi_ood = (RadioButton) findViewById(R.id.rdi_ood);
        //rdi_online_payment = (RadioButton) findViewById(R.id.rdi_online_payment);
        payButton = (Button) findViewById(R.id.payButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text_name = (TextView) findViewById(R.id.text_name);
        cmb_cuahang = (AutoCompleteTextView) findViewById(R.id.cmb_cuahang);
        recycler_store = (RecyclerView) findViewById(R.id.recycler_store);


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
        getAllStore();

        cmb_cuahang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String storeName = cmb_cuahang.getText().toString();
                storeId = "";
                if (storeName.compareTo(storeList.get(i).getStoreName()) == 0) {
                    storeId = storeList.get(i).getStoreId();
                    //Toast.makeText(PlaceOrderActivity.this, "Store Id " + storeId, Toast.LENGTH_SHORT).show();
                }
//                for(int j = 0; j < storeList.size(); j++){
//
//                }
            }
        });
    }

    //******** Stripe
    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Float amount = Float.valueOf(txt_total_cash.getText().toString());
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
                .enqueue(new PlaceOrderActivity.PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);

        payButton.setOnClickListener((View view) -> {
            getOrderNumer(false);
            try {
                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    stripe.confirmPayment(this, confirmParams);
                    cleanCard();
                    displayAlert(
                            "Thông báo",
                            "Thanh toán thành công.");
                }
            } catch (Exception e) {
                displayAlert(
                        "Thông báo",
                        "Thanh toán thành công.");
//                cleanCard();
//                finish();
            }

        });
    }
    //******** Stripe

    private void getOrderNumer(boolean isOnlinePayment) {
        orderId = UUID.randomUUID().toString();
        if (!isOnlinePayment) {
            compositeDisposable.add(cartDataSource.getAllCart2(Common.currentUser.getUserphone())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {
                        compositeDisposable.add(iMcDonaldsAPI.createOrder(Common.API_KEY, orderId, Common.Imei, Common.currentUser.getUserphone(), storeId, false, Common.totalCash, "WAITING", "Y", txt_user_address.getText().toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(createOrderModel -> {
                                    if (createOrderModel.isSuccess()) {
                                        compositeDisposable.add(iMcDonaldsAPI.createOrderDetail(Common.API_KEY, orderId, new Gson().toJson(cartItems))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(updateOrderModel -> {
                                                    if (updateOrderModel.isSuccess()) {
                                                        cartDataSource.cleanCart(Common.currentUser.getUserphone())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new SingleObserver<Integer>() {
                                                                    @Override
                                                                    public void onSubscribe(@NonNull Disposable d) {

                                                                    }

                                                                    @Override
                                                                    public void onSuccess(@NonNull Integer integer) {
//                                                                        displayAlert(
//                                                                                "Thông báo",
//                                                                                "Thanh toán thành công.");
                                                                        //cleanCard();
//                                                                        Intent homeActivity = new Intent(DonHangActivity.this, HomeActivity.class);
//                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                                        startActivity(homeActivity);

                                                                    }

                                                                    @Override
                                                                    public void onError(@NonNull Throwable e) {
                                                                        Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void cleanCard() {
        cartDataSource.cleanCart(Common.currentUser.getUserphone())
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
                        Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        stripe.onPaymentResult(requestCode, data, new PlaceOrderActivity.PaymentResultCallback(this));
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
        private final WeakReference<PlaceOrderActivity> activityRef;

        PayCallback(@NonNull PlaceOrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final PlaceOrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final PlaceOrderActivity activity = activityRef.get();
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
        private final WeakReference<PlaceOrderActivity> activityRef;

        PaymentResultCallback(@NonNull PlaceOrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final PlaceOrderActivity activity = activityRef.get();
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
            final PlaceOrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }


    private void initView() {
        ButterKnife.bind(this);
        txt_user_phone.setText(Common.currentUser.getUserphone());
        text_name.setText(Common.currentUser.getName());
        txt_total_cash.setText(Common.totalCash.toString());
        txt_user_address.setText(Common.currentUser.getAddress());
        txt_total_cash.setEnabled(false);
        text_name.setEnabled(false);
        txt_user_phone.setEnabled(false);


        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        btn_add_new_address.setOnClickListener(v -> {
//            isAddNewAddr = true;
//            ckb_default_address.setChecked(false);
//            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
//                    .inflate(R.layout.layout_add_new_address, null);
//
//            EditText edt_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_add_new_address);
//            edt_new_address.setText(txt_new_address.getText().toString());
//            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
//                    .setTitle("Add New Address")
//                    .setView(layout_add_new_address)
//                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
//                    .setPositiveButton("Thêm mới", ((dialog, which) -> txt_new_address.setText(edt_new_address.getText().toString())));
//
//            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
//            addNewAddressDialog.show();
//        });

        edt_date.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

//        payButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isSelectDate) {
//                    Toast.makeText(PlaceOrderActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        });
    }

    private void getAllStore() {
        compositeDisposable.add(iMcDonaldsAPI.getStore(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(storeModel -> {
                            EventBus.getDefault().post(new StoreLoadEvent(true, storeModel.getResult()));
                        },
                        throwable -> {
                            Toast.makeText(this, "[LOAD STORE]", Toast.LENGTH_SHORT).show();
                        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processStoreLoadEvent(StoreLoadEvent event) {
        if (event.isSuccess()) {
//            recycler_store.setHasFixedSize(true);
//            recycler_store.setLayoutManager(new LinearLayoutManager(this));
//            recycler_store.setAdapter(new StoreAdapter(PlaceOrderActivity.this, event.getStoreList()));
//
            storeList = event.getStoreList();

            List<String> storeName = new ArrayList<String>();
            for (Store store : storeList) {
                storeName.add(store.getStoreName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, storeName);
            cmb_cuahang.setAdapter(arrayAdapter);


        } else {
            Toast.makeText(this, "[STORE LOAD]", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectDate = true;
        edt_date.setText(new StringBuilder("")
                .append(monthOfYear + 1)
                .append("/")
                .append(dayOfMonth + 1)
                .append("/")
                .append(year));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}