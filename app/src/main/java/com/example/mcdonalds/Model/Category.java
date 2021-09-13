package com.example.mcdonalds.Model;

public class Category {
    private String categoryid, categoryname, description, categoryimg;

    public Category(String categoryId, String categoryName, String description, String categoryImg) {
        categoryid = categoryId;
        categoryname = categoryName;
        description = description;
        categoryimg = categoryImg;
    }

    public String getCategoryId() {
        return categoryid;
    }

    public void setCategoryId(String categoryId) {
        categoryid = categoryId;
    }

    public String getCategoryName() {
        return categoryname;
    }

    public void setCategoryName(String categoryName) {
        categoryname = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public String getCategoryImg() {
        return categoryimg;
    }

    public void setCategoryImg(String categoryImg) {
        categoryimg = categoryImg;
    }
}
