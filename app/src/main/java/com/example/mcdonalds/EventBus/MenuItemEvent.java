package com.example.mcdonalds.EventBus;

import com.example.mcdonalds.Model.Category;

public class MenuItemEvent {
    private boolean success;
    private Category category;

    public MenuItemEvent(boolean success, Category category) {
        this.success = success;
        this.category = category;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
