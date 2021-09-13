package com.example.mcdonalds.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mcdonalds.Model.Background;
import com.example.mcdonalds.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    List<Background> backgroundList;

    public SliderAdapter(List<Background> backgroundList) {
        this.backgroundList = backgroundList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
//viewHolder.imageView.setImageURI(backgroundList.get(position).getBackgroundImg());
        Picasso.get().load(backgroundList.get(position).getBackgroundimg()).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return backgroundList.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_view);
        }
    }
}
