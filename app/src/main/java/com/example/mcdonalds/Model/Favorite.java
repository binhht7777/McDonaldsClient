package com.example.mcdonalds.Model;

public class Favorite {
    private String FoodId, FoodName, FoodImg;
    private Integer Count;

    public Favorite(String foodId, String foodName, String foodImg, Integer count) {
        FoodId = foodId;
        FoodName = foodName;
        FoodImg = foodImg;
        Count = count;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodImg() {
        return FoodImg;
    }

    public void setFoodImg(String foodImg) {
        FoodImg = foodImg;
    }

    public Integer getCount() {
        return Count;
    }

    public void setCount(Integer count) {
        Count = count;
    }
}
