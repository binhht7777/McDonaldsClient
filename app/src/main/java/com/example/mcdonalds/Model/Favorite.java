package com.example.mcdonalds.Model;

public class Favorite {
    private String foodid, foodname, foodimg;
    private Integer count;

    public Favorite(String foodid, String foodname, String foodimg, Integer count) {
        this.foodid = foodid;
        this.foodname = foodname;
        this.foodimg = foodimg;
        this.count = count;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
