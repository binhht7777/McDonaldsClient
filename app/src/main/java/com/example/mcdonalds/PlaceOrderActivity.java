package com.example.mcdonalds;

import androidx.annotation.NonNull;

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
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText edt_date;
    TextView txt_total_cash, txt_user_phone, txt_user_address, txt_new_address, text_name;
    Button btn_add_new_address, btn_process;
    CheckBox ckb_default_address;
    RadioButton rdi_ood, rdi_online_payment;
    Toolbar toolbar;
    AutoCompleteTextView cmb_cuahang;
    List<Store> storeList;
    String storeId = "", orderId = "";
    RecyclerView recycler_store;


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
        edt_date = (EditText) findViewById(R.id.edt_date);
        txt_total_cash = (TextView) findViewById(R.id.txt_total_cash);
        txt_user_phone = (TextView) findViewById(R.id.text_user_phone);
        txt_user_address = (TextView) findViewById(R.id.txt_user_address);
//        txt_new_address = (TextView) findViewById(R.id.txt_new_address);
//        btn_add_new_address = (Button) findViewById(R.id.btn_new_address);
//        ckb_default_address = (CheckBox) findViewById(R.id.chk_default_address);
        rdi_ood = (RadioButton) findViewById(R.id.rdi_ood);
        rdi_online_payment = (RadioButton) findViewById(R.id.rdi_online_payment);
        btn_process = (Button) findViewById(R.id.btn_processed);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text_name = (TextView) findViewById(R.id.text_name);
        cmb_cuahang = (AutoCompleteTextView) findViewById(R.id.cmb_cuahang);
        recycler_store = (RecyclerView) findViewById(R.id.recycler_store);

        init();
        initView();
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

    private void initView() {
        ButterKnife.bind(this);
        txt_user_phone.setText(Common.currentUser.getUserPhone());
        text_name.setText(Common.currentUser.getName());
        txt_total_cash.setText(Common.totalCash.toString());
        txt_total_cash.setEnabled(false);
        text_name.setEnabled(false);
        txt_user_phone.setEnabled(false);


        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn_add_new_address.setOnClickListener(v -> {
            isAddNewAddr = true;
            ckb_default_address.setChecked(false);
            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                    .inflate(R.layout.layout_add_new_address, null);

            EditText edt_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_add_new_address);
            edt_new_address.setText(txt_new_address.getText().toString());
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(layout_add_new_address)
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Thêm mới", ((dialog, which) -> txt_new_address.setText(edt_new_address.getText().toString())));

            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
            addNewAddressDialog.show();
        });

        edt_date.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });
        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectDate) {
                    Toast.makeText(PlaceOrderActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isAddNewAddr) {
                    if (!ckb_default_address.isChecked()) {
                        Toast.makeText(PlaceOrderActivity.this, "Please choose default address or set new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (rdi_ood.isChecked()) {
                    getOrderNumer(false);
                } else if (rdi_online_payment.isChecked()) {
                }
            }
        });
    }

    private void getOrderNumer(boolean isOnlinePayment) {
        if (!isOnlinePayment) {
            orderId = UUID.randomUUID().toString();
            String address = txt_user_address.getText().toString();
            compositeDisposable.add(cartDataSource.getAllCart2(Common.currentUser.getUserPhone())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {
                        compositeDisposable.add(iMcDonaldsAPI.createOrder(Common.API_KEY, orderId, Common.Imei, Common.currentUser.getUserPhone(), storeId, true, Common.totalCash, address)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(createOrderModel -> {
                                    if (createOrderModel.isSuccess()) {
                                        String jSon = new Gson().toJson(cartItems);
                                        compositeDisposable.add(iMcDonaldsAPI.createOrderDetail(Common.API_KEY, orderId, jSon)
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
                                                                        Toast.makeText(PlaceOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(homeActivity);
                                                                        finish();
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
                                    Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));
                    }, throwable -> {
                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processStoreLoadEvent(StoreLoadEvent event) {
        if (event.isSuccess()) {
            recycler_store.setHasFixedSize(true);
            recycler_store.setLayoutManager(new LinearLayoutManager(this));
            recycler_store.setAdapter(new StoreAdapter(PlaceOrderActivity.this, event.getStoreList()));

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
                .append(dayOfMonth)
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvent event) {
        txt_total_cash.setText(String.valueOf(event.getCash()));
    }
}