package com.example.mcdonalds.Model;

public class FavoriteOnlyId {
    private String FoodId;

    public FavoriteOnlyId(String foodId) {
        this.FoodId = foodId;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }
}
