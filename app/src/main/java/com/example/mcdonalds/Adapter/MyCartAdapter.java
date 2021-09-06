package com.example.mcdonalds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.CartItem;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.EventBus.CalculatePriceEvent;
import com.example.mcdonalds.Interface.IOnImageViewAdapterClickListener;
import com.example.mcdonalds.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    CartDataSource cartDataSource;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(cartItemList.get(position).getFoodImage()).into(holder.img_food);
        holder.txt_food_name.setText(cartItemList.get(position).getFoodName());
        holder.txt_food_price.setText(String.valueOf(cartItemList.get(position).getFoodPrice()));
        holder.txt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
        float Total = cartItemList.get(position).getFoodPrice() * cartItemList.get(position).getFoodQuantity();
        holder.txt_price_new.setText(String.valueOf(Total));
//        holder.txt_extra_price.setText(new StringBuilder("Extra Price(VND): +")
//                .append(cartItemList.get(position).getFoodPrice()));

        holder.setListener(((view, position1, isDecrease, isDelete) -> {
            if (!isDelete) {
                if (isDecrease) {
                    if (cartItemList.get(position).getFoodQuantity() > 1) {
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() - 1);
                    }
                } else {
                    if (cartItemList.get(position).getFoodQuantity() < 99) {
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() + 1);
                    }
                }
                cartDataSource.updateCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NotNull Integer integer) {
                                holder.txt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Toast.makeText(context, "[Update Cart]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                cartDataSource.deleteCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NotNull Integer integer) {
                                notifyItemRemoved(position);
                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Toast.makeText(context, "[Delete Cart]", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.txt_price_new)
        TextView txt_price_new;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_quantity)
        TextView txt_quantity;
        @BindView(R.id.img_food)
        ImageView img_food;
        @BindView(R.id.img_delete_food)
        ImageView img_delete_food;
        @BindView(R.id.img_decrease)
        Button img_decrease;
        @BindView(R.id.img_increate)
        Button img_increate;
//        @BindView(R.id.txt_extra_price)
//        TextView txt_extra_price;

        IOnImageViewAdapterClickListener listener;

        public void setListener(IOnImageViewAdapterClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            img_decrease.setOnClickListener(this);
            img_increate.setOnClickListener(this);
            img_delete_food.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == img_decrease) {
                listener.onCalculatePriceListener(view, getAdapterPosition(), true, false);
            } else if (view == img_increate) {
                listener.onCalculatePriceListener(view, getAdapterPosition(), false, false);
            } else if (view == img_delete_food) {
                listener.onCalculatePriceListener(view, getAdapterPosition(), true, true);
            }
        }
    }
}
