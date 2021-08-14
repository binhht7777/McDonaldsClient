package com.example.mcdonalds.EventBus;

import com.example.mcdonalds.Model.Category;

import java.util.List;

public class CategoryLoadEvent {
    private boolean success;
    private String message;
    private List<Category> categoryList;

    public CategoryLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CategoryLoadEvent(boolean success, List<Category> categoryList) {
        this.success = success;
        this.categoryList = categoryList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
