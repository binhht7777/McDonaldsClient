package com.example.mcdonalds;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
import com.example.mcdonalds.Adapter.CategoryAdapter;
import com.example.mcdonalds.Adapter.FavoriteSliderAdapter;
import com.example.mcdonalds.Adapter.SliderAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.EventBus.BackgroundLoadEvent;
import com.example.mcdonalds.EventBus.CategoryLoadEvent;
import com.example.mcdonalds.EventBus.FavoriteLoadEvent;
import com.example.mcdonalds.Model.Background;
import com.example.mcdonalds.Model.Category;
import com.example.mcdonalds.Model.Favorite;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.example.mcdonalds.Services.PicassoImageLoadingService;
import com.example.mcdonalds.ui.dashboard.DashboardFragment;
import com.example.mcdonalds.ui.dashboard.DashboardViewModel;
import com.example.mcdonalds.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mcdonalds.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity {


    private ActivityHomeBinding binding;

    TextView txtThucDon;
    SliderView sliderView;
    IMcDonaldsAPI iMcDonaldsAPI;
    BackgroundSliderAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.favorite_slider)
    Slider favorite_slider;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_user)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
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
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), DonHangActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_user:
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        sliderView=findViewById(R.id.image_slide);
        init();
        initView();
        LoadBackgroud();
        LoadFavorite();
    }

    private void LoadBackgroud() {
        compositeDisposable.add(iMcDonaldsAPI.getBackground(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(backgroundModel -> {
                            EventBus.getDefault().post(new BackgroundLoadEvent(true, backgroundModel.getResult()));
                        },
                        throwable -> {
                            EventBus.getDefault().post(new BackgroundLoadEvent(false, throwable.getMessage()));
                        }));
    }

    private void LoadFavorite() {
        compositeDisposable.add(iMcDonaldsAPI.getFavorite(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteModel -> {
                            EventBus.getDefault().post(new FavoriteLoadEvent(true, favoriteModel.getResult()));
                        },
                        throwable -> {
                            EventBus.getDefault().post(new FavoriteLoadEvent(false, throwable.getMessage()));
                        }));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processBackgroundLoadEvent(BackgroundLoadEvent event) {
        if (event.isSuccess()) {
            displayBanner(event.getBackgroundList());
        } else {
            Toast.makeText(this, "[BACKGROUND LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processFavoriteLoadEvent(FavoriteLoadEvent event) {
        if (event.isSuccess()) {
            displayFavorites(event.getFavoriteList());
        } else {
            Toast.makeText(this, "[FAVORITE LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

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

    private void displayBanner(List<Background> backgroundList) {
        SliderAdapter adapter = new SliderAdapter(backgroundList);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

    }

    private void displayFavorites(List<Favorite> favoriteList) {
        favorite_slider.setAdapter(new FavoriteSliderAdapter(favoriteList));
    }
}