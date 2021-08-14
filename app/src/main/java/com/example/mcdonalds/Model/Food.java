package com.example.mcdonalds.Model;

public class Food {
    private String CategoryId, FoodId, FoodName, FoodImg;
    private float Price;

    public Food(String categoryId, String foodId, String foodName, String foodImg, float price) {
        CategoryId = categoryId;
        FoodId = foodId;
        FoodName = foodName;
        FoodImg = foodImg;
        Price = price;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
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

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }
}
