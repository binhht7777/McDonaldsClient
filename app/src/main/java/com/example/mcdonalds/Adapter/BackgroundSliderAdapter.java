package com.example.mcdonalds.Adapter;

import com.example.mcdonalds.Model.Background;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class BackgroundSliderAdapter extends SliderAdapter {
    List<Background> backgroundList;

    public BackgroundSliderAdapter(List<Background> backgroundList) {
        this.backgroundList = backgroundList;
    }

    @Override
    public int getItemCount() {
        return backgroundList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(backgroundList.get(position).getBackgroundimg());
    }
}
