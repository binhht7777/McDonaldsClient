package com.example.mcdonalds.Model;

public class Category {
    private String CategoryId, CategoryName, Description, CategoryImg;

    public Category(String categoryId, String categoryName, String description, String categoryImg) {
        CategoryId = categoryId;
        CategoryName = categoryName;
        Description = description;
        CategoryImg = categoryImg;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategoryImg() {
        return CategoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        CategoryImg = categoryImg;
    }
}
