package com.example.mcdonalds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mcdonalds.Common.Common;

import com.example.mcdonalds.EventBus.FoodListEvent;
import com.example.mcdonalds.FoodActivity;
import com.example.mcdonalds.Interface.IOnRecyclerViewClickListener;
import com.example.mcdonalds.Model.Category;
import com.example.mcdonalds.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(categoryList.get(position).getCategoryImg()).into(holder.img_catogory);
        holder.txt_catogory_name.setText(new StringBuilder(categoryList.get(position).getCategoryName()));
        holder.txt_description.setText(new StringBuilder(categoryList.get(position).getDescription()));

        holder.setListener((view, position1) -> {
            Common.currentCategory = categoryList.get(position);

            EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(position)));
            context.startActivity(new Intent(context, FoodActivity.class));
        });

        holder.img_catogory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentCategory = categoryList.get(position);
                EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(position)));
                context.startActivity(new Intent(context, FoodActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_catogory_name)
        TextView txt_catogory_name;
        @BindView(R.id.txt_description)
        TextView txt_description;
        @BindView(R.id.img_catogory)
        ImageView img_catogory;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onCLick(v, getAdapterPosition());
        }
    }
}
