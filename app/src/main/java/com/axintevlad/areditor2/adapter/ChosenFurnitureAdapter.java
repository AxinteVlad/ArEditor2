package com.axintevlad.areditor2.adapter;

/**
 * Created by vlad__000 on 22.05.2020.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axintevlad.areditor2.R;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChosenFurnitureAdapter extends RecyclerView.Adapter<ChosenFurnitureAdapter.ChosenViewHolder> {

    private Context mContext;
    private List<String> chosenFurnitureImagesList;
    private ChosenFurnitureAdapter.ItemClickListener mClickListener;

    public class ChosenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView photo;

        public ChosenViewHolder(View view) {
            super(view);
            photo = view.findViewById(R.id.chosen_item_adapter_photo);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

    }


    public ChosenFurnitureAdapter(Context mContext, List<String> chosenFurnitureimagesList) {
        this.mContext = mContext;
        this.chosenFurnitureImagesList = chosenFurnitureimagesList;
    }

    @Override
    public ChosenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chosen, parent, false);

        return new ChosenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChosenViewHolder holder, final int position) {
        final String item = chosenFurnitureImagesList.get(position);
        holder.photo.setImageDrawable(mContext.getResources().getDrawable(mContext.getResources().getIdentifier(item, "drawable", mContext.getPackageName())));
    }


    @Override
    public int getItemCount() {
        return chosenFurnitureImagesList.size();
    }

    // allows clicks events to be caught
    void setClickListener(ChosenFurnitureAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
