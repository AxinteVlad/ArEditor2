package com.axintevlad.areditor2.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

/**
 * Created by vlad__000 on 22.05.2020.
 */
@IgnoreExtraProperties
public class FurnitureObject {
    private String title, category, provider, photoId;
    private int price;
    private float quality;
    private String itemId;
    private boolean isSelected;

    public FurnitureObject(String title, String category, String provider, int price, float quality, String photoId) {
        this.category = category;
        this.photoId = photoId;
        this.price = price;
        this.title = title;
        this.provider = provider;
        this.quality = quality;
        this.itemId = UUID.randomUUID().toString();
        isSelected = false;
    }

    public FurnitureObject() {
        this.itemId = UUID.randomUUID().toString();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public String getItemId() {
        return itemId;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}
