package com.axintevlad.areditor2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axintevlad.areditor2.R;
import com.axintevlad.areditor2.model.FurnitureObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vlad__000 on 22.05.2020.
 */

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.MyViewHolder> {

    private Context mContext;
    private List<FurnitureObject> furnitureObjectList, furnitureObjectListCopy;
    private ItemClickListener mClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, price, category, provider, quality;
        public ImageView photo;
        public LinearLayout parentLayout;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_adapter_title);
            provider = view.findViewById(R.id.item_adapter_provider);
            quality = view.findViewById(R.id.item_adapter_quality);
            photo = view.findViewById(R.id.item_adapter_photo);
            price = view.findViewById(R.id.item_adapter_price);
            category = view.findViewById(R.id.item_adapter_category);
            parentLayout = view.findViewById(R.id.furniture_item_card);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }


    }


    public FurnitureAdapter(Context mContext, List<FurnitureObject> furnitureList) {
        this.mContext = mContext;
        this.furnitureObjectList = furnitureList;
        furnitureObjectListCopy = new ArrayList<>();
        furnitureObjectListCopy.addAll(furnitureList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FurnitureObject item = furnitureObjectList.get(position);
        holder.title.setText(item.getTitle());
        holder.provider.setText(item.getProvider());
        holder.price.setText(item.getPrice() + " RON");
        holder.quality.setText(Float.toString(item.getQuality()));
        holder.category.setText(item.getCategory());
        holder.photo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier(item.getPhotoId(), "drawable", mContext.getPackageName())));

        holder.parentLayout.setBackgroundColor(item.isSelected() ? Color.rgb(210, 210, 210) : Color.WHITE);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setSelected(!item.isSelected());
                holder.parentLayout.setBackgroundColor(item.isSelected() ? Color.rgb(210, 210, 210) : Color.WHITE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return furnitureObjectList.size();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void filter(String text) {
        furnitureObjectListCopy.clear();
        furnitureObjectListCopy.addAll(furnitureObjectList);
        furnitureObjectList.clear();
        if (text.isEmpty()) {
            furnitureObjectList.addAll(furnitureObjectListCopy);
        } else {
            text = text.toLowerCase();
            for (FurnitureObject item : furnitureObjectListCopy) {
                if (item.getTitle().toLowerCase().contains(text)) {
                    furnitureObjectList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void restoreData() {
        furnitureObjectList.clear();
        furnitureObjectList.addAll(furnitureObjectListCopy);
        notifyDataSetChanged();
    }
}