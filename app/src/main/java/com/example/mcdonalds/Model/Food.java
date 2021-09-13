package com.example.mcdonalds.Model;

public class Food {
    private String categoryid, foodid, foodname, foodimg;
    private float price;

    public Food(String categoryid, String foodid, String foodname, String foodimg, float price) {
        this.categoryid = categoryid;
        this.foodid = foodid;
        this.foodname = foodname;
        this.foodimg = foodimg;
        this.price = price;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodimg() {
        return foodimg;
    }

    public void setFoodimg(String foodimg) {
        this.foodimg = foodimg;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
