package com.example.mcdonalds.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.CartItem;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.Interface.IFoodDetailOrCartClickListener;
import com.example.mcdonalds.Model.Food;
import com.example.mcdonalds.R;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> implements Filterable {
    Context context;
    List<Food> foodList;
    List<Food> foodListOld;
    private SelectedFood selectedFood;

    CompositeDisposable completDisposable;
    IMcDonaldsAPI mcDonaldsAPI;
    CartDataSource cartDataSource;
    String orderDetailId;
    public void onStop() {
        completDisposable.clear();

    }

    public FoodAdapter(Context context, List<Food> foodList, SelectedFood selectedFood) {
        this.context = context;
        this.foodList = foodList;
        this.foodListOld = foodList;
        this.selectedFood = selectedFood;
        completDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        mcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    foodList = foodListOld;
                } else {
                    List<Food> list = new ArrayList<>();
                    for (Food food : foodListOld) {
                        if (food.getFoodname().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(food);
                        }
                    }
                    foodList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = foodList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                foodList = (List<Food>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Picasso.get().load(foodList.get(position).getFoodimg())
                .placeholder(null)
                .into(holder.img_food);
        holder.txt_food_name.setText(foodList.get(position).getFoodname());
        holder.txt_food_price.setText(String.valueOf(foodList.get(position).getPrice()) + " " + context.getString(R.string.currency));

        orderDetailId = UUID.randomUUID().toString();
        holder.setListener((view, position1, isDetail) -> {
            Common.currentFood = foodList.get(position);
            Common.globalFoodId = foodList.get(position).getFoodid();
            if (isDetail) {
//                context.startActivity(new Intent(context, FoodDetailActivity.class));
//                EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodList.get(position)));
                Toast.makeText(context, "YOU CLICK INFOMATION", Toast.LENGTH_SHORT).show();
            } else {
                // cart create
                CartItem cartItem = new CartItem();
                cartItem.setOrderDetailId(orderDetailId);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setFoodId(foodList.get(position).getFoodid());
                cartItem.setFoodName(foodList.get(position).getFoodname());
                cartItem.setFoodPrice(foodList.get(position).getPrice());
                cartItem.setFoodImage(foodList.get(position).getFoodimg());
                cartItem.setFoodQuantity(1);
                cartItem.setCategoryId(Common.currentCategory.getCategoryId());
                completDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(context, "Add to order", Toast.LENGTH_SHORT).show();
                                        },
                                        throwable -> {
                                            Toast.makeText(context, "[ADD ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public interface SelectedFood {
        void selectedFood(Food foodModel);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food)
        ImageView img_food;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.txt_food_price)
        TextView txt_food_price;

        @BindView(R.id.img_detail)
        ImageView img_detail;

        @BindView(R.id.img_card)
        ImageView img_card;

        @BindView(R.id.img_fav)
        ImageView img_fav;

        IFoodDetailOrCartClickListener listener;

        public void setListener(IFoodDetailOrCartClickListener listener) {
            this.listener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

            img_detail.setOnClickListener(this);
            img_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_detail) {
                listener.onFoodItemClickListener(v, getAdapterPosition(), true);
            } else if (v.getId() == R.id.img_card) {
                listener.onFoodItemClickListener(v, getAdapterPosition(), false);
            }
            selectedFood.selectedFood(foodList.get(getAdapterPosition()));
        }
    }
}
