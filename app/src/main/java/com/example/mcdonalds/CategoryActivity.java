package com.example.mcdonalds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.CategoryAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.EventBus.CategoryLoadEvent;
import com.example.mcdonalds.Model.Category;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.example.mcdonalds.Services.PicassoImageLoadingService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.recycler_category)
    RecyclerView recycler_category;

    CategoryAdapter adapter;
    IMcDonaldsAPI iMcDonaldsAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView tvHoten, tvPhone;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        tvHoten=(TextView)findViewById(R.id.tvHoten);
        tvPhone=(TextView)findViewById(R.id.tvPhone);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
                Fragment selectedFragmenet = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(),DonHangActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_user:
                        startActivity(new Intent(getApplicationContext(),UserActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        init();
        initView();
        LoadCategory();
    }

    private void LoadCategory() {
        compositeDisposable.add(iMcDonaldsAPI.getCategory(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categorydModel -> {
                            EventBus.getDefault().post(new CategoryLoadEvent(true, categorydModel.getResult()));

                        },
                        throwable -> {
                            EventBus.getDefault().post(new CategoryLoadEvent(false, throwable.getMessage()));
                        }));
    }

    private void initView() {
        ButterKnife.bind(this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_category.setLayoutManager(mLayoutManager);
        tvHoten.setText(Common.currentUser.getName());
        tvPhone.setText(Common.currentUser.getUserPhone());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recycler_category.setLayoutManager(layoutManager);
//        recycler_category.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
        Slider.init(new PicassoImageLoadingService());
    }

    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processCategoryLoadEvent(CategoryLoadEvent event) {
        if (event.isSuccess()) {
            displayCategory(event.getCategoryList());
        } else {
            Toast.makeText(this, "[CATEGORY LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCategory(List<Category> categoryList) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList);
        recycler_category.setAdapter(categoryAdapter);
    }
}