//package com.example.mcdonalds;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
//import com.example.mcdonalds.Adapter.MyCartAdapter;
//import com.example.mcdonalds.Common.Common;
//import com.example.mcdonalds.Database.CartDataSource;
//import com.example.mcdonalds.Database.CartDatabase;
//import com.example.mcdonalds.Database.CartItem;
//import com.example.mcdonalds.Database.LocalCartDataSource;
//import com.example.mcdonalds.EventBus.CalculatePriceEvent;
//import com.example.mcdonalds.EventBus.SendTotalCashEvent;
//import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
//import com.example.mcdonalds.Retrofit.RetrofitClient;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//import io.reactivex.SingleObserver;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
//public class CartListActivity extends AppCompatActivity {
//    Toolbar toolbar;
//    RecyclerView recycler_cart;
//    TextView txt_final_price;
//    Button btn_order;
//    RadioButton rad_Order1, rad_Order2;
//    AutoCompleteTextView cmb_CuaHang;
//
//    IMcDonaldsAPI iMcDonaldsAPI;
//    BackgroundSliderAdapter adapter;
//    CompositeDisposable compositeDisposable = new CompositeDisposable();
//    CartDataSource cartDataSource;
//
//    @Override
//    protected void onDestroy() {
//        compositeDisposable.clear();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart_list);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
//        txt_final_price = (TextView) findViewById(R.id.txt_final_price);
//        btn_order = (Button) findViewById(R.id.btn_order);
//        rad_Order1 = (RadioButton) findViewById(R.id.rad_order1);
//        rad_Order2 = (RadioButton) findViewById(R.id.rad_order2);
//        cmb_CuaHang = (AutoCompleteTextView) findViewById(R.id.cmb_CuaHang);
//
//        init();
//        initView();
//        getAllItemInCart();
//    }
//
//    private void getAllItemInCart() {
//        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUserPhone(),
//                Common.currentFood.getFoodId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(cartItems -> {
//                    if (cartItems.isEmpty()) {
//                        btn_order.setText(getString(R.string.empty_cart));
//                        btn_order.setEnabled(false);
//                        btn_order.setBackgroundResource(android.R.color.darker_gray);
//                    } else {
//                        btn_order.setText(getString(R.string.place_order));
//                        btn_order.setEnabled(false);
//                        btn_order.setBackgroundResource(R.color.design_default_color_primary);
//                        MyCartAdapter adapter = new MyCartAdapter(CartListActivity.this, cartItems);
//                        recycler_cart.setAdapter(adapter);
//                        calculateCartTotalPrice();
//                    }
//
//                }, throwable -> {
//                    Toast.makeText(this, "[GET CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }));
//    }
//
//    private void calculateCartTotalPrice() {
//        cartDataSource.sumPrice(Common.currentUser.getUserPhone(), Common.currentFood.getFoodId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<Long>() {
//                    @Override
//                    public void onSubscribe(@NotNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(@NotNull Long aLong) {
//                        if (aLong <= 0) {
//                            btn_order.setText(getString(R.string.empty_cart));
//                            btn_order.setEnabled(false);
//                            btn_order.setBackgroundResource(android.R.color.darker_gray);
//                        } else {
//                            btn_order.setText(getString(R.string.place_order));
//                            btn_order.setEnabled(true);
//                            btn_order.setBackgroundResource(R.color.DoDam);
//                        }
//                        txt_final_price.setText(String.valueOf(aLong));
//                    }
//
//                    @Override
//                    public void onError(@NotNull Throwable e) {
//                        if (e.getMessage().contains("Query returned empty")) {
//                            txt_final_price.setText("0");
//                        } else {
//                            Toast.makeText(CartListActivity.this, "[Sum Cart]", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void initView() {
//        ButterKnife.bind(this);
//        rad_Order1.isChecked();
//        toolbar.setTitle(getString(R.string.cart));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        recycler_cart.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recycler_cart.setLayoutManager(layoutManager);
//        recycler_cart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
//
//        if (rad_Order1.isChecked() == true) {
//            cmb_CuaHang.setEnabled(false);
//        } else {
//            cmb_CuaHang.setEnabled(true);
//        }
//
//        btn_order.setOnClickListener(v -> {
//            EventBus.getDefault().postSticky(new SendTotalCashEvent(txt_final_price.getText().toString()));
//            Toast.makeText(this, "Bạn đặt hàng thành công", Toast.LENGTH_SHORT).show();
//        });
//
//        rad_Order1.setOnCheckedChangeListener((compoundButton, b) -> {
//            cmb_CuaHang.setEnabled(false);
//        });
//
//        rad_Order2.setOnCheckedChangeListener((compoundButton, b) -> {
//            cmb_CuaHang.setEnabled(true);
//        });
//    }
//
//    private void init() {
//        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
//        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void calculatePrice(CalculatePriceEvent event) {
//        if (event != null) {
//            calculateCartTotalPrice();
//        }
//    }
//}