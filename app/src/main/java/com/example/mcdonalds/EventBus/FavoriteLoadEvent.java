package com.example.mcdonalds.EventBus;

import com.example.mcdonalds.Model.Background;
import com.example.mcdonalds.Model.Favorite;

import java.util.List;

public class FavoriteLoadEvent {
    private boolean success;
    private String message;
    private List<Favorite> favoriteList;

    public FavoriteLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public FavoriteLoadEvent(boolean success, List<Favorite> favoriteList) {
        this.success = success;
        this.favoriteList = favoriteList;
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

    public List<Favorite> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
    }
}
