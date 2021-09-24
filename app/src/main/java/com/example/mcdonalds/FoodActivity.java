package com.example.mcdonalds;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mcdonalds.Adapter.FoodAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.EventBus.CategoryLoadEvent;
import com.example.mcdonalds.EventBus.FavoriteLoadEvent;
import com.example.mcdonalds.EventBus.FoodListEvent;
import com.example.mcdonalds.Model.Food;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodActivity extends AppCompatActivity {
    IMcDonaldsAPI iMcDonaldsAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.recycler_food)
    RecyclerView recycler_food;

    @BindView(R.id.txt_category_name)
    TextView txt_category_name;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FoodAdapter adapter;


    // Khai bao 1 recyclerview moi
    RecyclerView rcvFoodList;
    FoodAdapter foodAdapter;
    SearchView searchView;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if (adapter != null) {
            adapter.onStop();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        init();
        initView();


    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);
        recycler_food.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        // Khai bao 1 recyclerview moi
        rcvFoodList = findViewById(R.id.recycler_food);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFoodList.setLayoutManager(linearLayoutManager);
        LoadFavorite();
    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteFood();
    }

    private void LoadFavorite() {
        compositeDisposable.add(iMcDonaldsAPI.getFavorite2(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteModel -> {
                            Common.getFavoriteAll = favoriteModel.getResult();
                        },
                        throwable -> {
                            Toast.makeText(this, "[GET FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
    }

    private void loadFavoriteFood() {
        compositeDisposable.add(iMcDonaldsAPI.getAllFavorite2(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteOnlyIdModel -> {
                            if (favoriteOnlyIdModel.isSuccess()) {
                                if (favoriteOnlyIdModel.getResult() != null && favoriteOnlyIdModel.getResult().size() > 0) {
                                    Common.currentFavoriteFoodALL = favoriteOnlyIdModel.getResult();
                                } else {
                                    Common.currentFavoriteFoodALL = new ArrayList<>();
                                }
                            } else {
//                                Toast.makeText(this, "[GET FAVORITE]" + favoriteOnlyIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        , throwable -> {
                            Toast.makeText(this, "[GET FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadFoodByCategory(FoodListEvent event) {
        if (event.isSuccess()) {
            txt_category_name.setText(event.getCategory().getCategoryName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            compositeDisposable.add(iMcDonaldsAPI.getFoodByCategory(Common.API_KEY,
                    event.getCategory().getCategoryId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                                if (foodModel.isSuccess()) {
                                    adapter = new FoodAdapter(this, foodModel.getResult(), this::selectedFood);
                                    recycler_food.setAdapter(adapter);

                                    // set adapter search food
                                    foodAdapter = new FoodAdapter(this, foodModel.getResult(), this::selectedFood);
                                    rcvFoodList.setAdapter(foodAdapter);
                                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                                    rcvFoodList.addItemDecoration(itemDecoration);

                                } else {
                                    Toast.makeText(this, "[GET FOOD RESULT]" + foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(this, "[GET FOOD]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.nav_food_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                foodAdapter.getFilter().filter(query);
                recycler_food.setAdapter(foodAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foodAdapter.getFilter().filter(newText);
                recycler_food.setAdapter(foodAdapter);
                return true;
            }
        });

        return true;
    }

    public void selectedFood(Food foodModel) {
        // BinhPT06 - Get  categoryId and send to new activity
//        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailActivity.class);
//        foodDetail.putExtra("FoodId", foodModel.getId());
//        startActivity(foodDetail);
    }
}