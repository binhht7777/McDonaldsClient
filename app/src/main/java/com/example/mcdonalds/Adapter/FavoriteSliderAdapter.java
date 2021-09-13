package com.example.mcdonalds.Adapter;

import com.example.mcdonalds.Model.Favorite;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class FavoriteSliderAdapter extends SliderAdapter {
    List<Favorite> favoriteList;

    public FavoriteSliderAdapter(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(favoriteList.get(position).getFoodimg());
    }
}
