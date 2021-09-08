package com.example.mcdonalds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
import com.example.mcdonalds.Adapter.FoodAdapter;
import com.example.mcdonalds.Adapter.MyCartAdapter;
import com.example.mcdonalds.Adapter.SliderAdapter;
import com.example.mcdonalds.Adapter.StoreAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.EventBus.BackgroundLoadEvent;
import com.example.mcdonalds.EventBus.CalculatePriceEvent;
import com.example.mcdonalds.EventBus.SendTotalCashEvent;
import com.example.mcdonalds.EventBus.StoreLoadEvent;
import com.example.mcdonalds.Model.Background;
import com.example.mcdonalds.Model.Store;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DonHangActivity extends AppCompatActivity {
    //    Toolbar toolbar;
    RecyclerView recycler_cart, recycler_store;
    TextView txt_final_price;
    Button btn_order;
    RadioButton rad_OrderQuay, rad_OrderTruoc;
    AutoCompleteTextView cmb_cuahang;

    IMcDonaldsAPI iMcDonaldsAPI;
    BackgroundSliderAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    boolean chkOrder = false;
    List<Store> storeList;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
        txt_final_price = (TextView) findViewById(R.id.txt_final_price);
        btn_order = (Button) findViewById(R.id.btn_order);
        rad_OrderQuay = (RadioButton) findViewById(R.id.rad_orderquay);
        rad_OrderTruoc = (RadioButton) findViewById(R.id.rad_ordertruoc);
        recycler_store = (RecyclerView) findViewById(R.id.recycler_store);
        cmb_cuahang = (AutoCompleteTextView) findViewById(R.id.cmb_cuahang);

        init();
        initView();
        getAllItemInCart();
        getAllStore();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
                Fragment selectedFragmenet = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_notifications:
                        return true;
                    case R.id.navigation_user:
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        cmb_cuahang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String storeName = cmb_cuahang.getText().toString();
                String storeId = "";
                if (storeName.compareTo(storeList.get(i).getStoreName()) == 0) {
                    storeId = storeList.get(i).getStoreId();
                    Toast.makeText(DonHangActivity.this, "Store Id " + storeId, Toast.LENGTH_SHORT).show();
                }
//                for(int j = 0; j < storeList.size(); j++){
//
//                }
            }
        });
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

    private void getAllItemInCart() {
        compositeDisposable.add(cartDataSource.getAllCart2(Common.currentUser.getUserPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    if (cartItems.isEmpty()) {
                        btn_order.setText(getString(R.string.empty_cart));
                        btn_order.setEnabled(false);
                        btn_order.setBackgroundResource(android.R.color.darker_gray);
                    } else {
                        btn_order.setText(getString(R.string.place_order));
                        btn_order.setEnabled(true);
                        btn_order.setBackgroundResource(R.color.design_default_color_primary);
                        MyCartAdapter adapter = new MyCartAdapter(DonHangActivity.this, cartItems);
                        recycler_cart.setAdapter(adapter);
                        calculateCartTotalPrice();
                    }

                }, throwable -> {
                    Toast.makeText(this, "[GET CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));
    }

    private void calculateCartTotalPrice() {
        cartDataSource.sumPrice2(Common.currentUser.getUserPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Long aLong) {
                        if (aLong <= 0) {
                            btn_order.setText(getString(R.string.empty_cart));
                            btn_order.setEnabled(false);
                            btn_order.setBackgroundResource(android.R.color.darker_gray);
                        } else {
                            btn_order.setText(getString(R.string.place_order));
                            btn_order.setEnabled(true);
                            btn_order.setBackgroundResource(R.color.DoDam);
                        }
                        txt_final_price.setText(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (e.getMessage().contains("Query returned empty")) {
                            txt_final_price.setText("0");
                        } else {
                            Toast.makeText(DonHangActivity.this, "[Sum Cart]", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        ButterKnife.bind(this);
//        toolbar.setTitle(getString(R.string.cart));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (rad_OrderQuay.isChecked()) {
            cmb_cuahang.setEnabled(false);
        } else {
            cmb_cuahang.setEnabled(true);
        }

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btn_order.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new SendTotalCashEvent(txt_final_price.getText().toString()));
            Toast.makeText(this, "Bạn đặt hàng thành công", Toast.LENGTH_SHORT).show();
        });

        rad_OrderQuay.setOnClickListener(view -> {
            chkOrder = false;
            checkOrder();
        });

        rad_OrderTruoc.setOnClickListener(view -> {
            chkOrder = true;
            checkOrder();
        });
    }

    private void checkOrder() {
        if (chkOrder == false) {
            cmb_cuahang.setEnabled(false);
            btn_order.setText("Thanh Toán");
        } else {
            cmb_cuahang.setEnabled(true);
            btn_order.setText("Đặt Hàng");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processStoreLoadEvent(StoreLoadEvent event) {
        if (event.isSuccess()) {
            recycler_store.setHasFixedSize(true);
            recycler_store.setLayoutManager(new LinearLayoutManager(this));
            recycler_store.setAdapter(new StoreAdapter(DonHangActivity.this, event.getStoreList()));

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
    public void calculatePrice(CalculatePriceEvent event) {
        if (event != null) {
            calculateCartTotalPrice();
        }
    }
}