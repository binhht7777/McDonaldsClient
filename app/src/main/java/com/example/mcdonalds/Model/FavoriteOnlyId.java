package com.example.mcdonalds.Model;

public class FavoriteOnlyId {
    private String foodid;

    public FavoriteOnlyId(String foodid) {
        this.foodid = foodid;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }
}
