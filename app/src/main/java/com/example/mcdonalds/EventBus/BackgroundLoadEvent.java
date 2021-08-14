package com.example.mcdonalds.EventBus;

import com.example.mcdonalds.Model.Background;

import java.util.List;

public class BackgroundLoadEvent {
    private boolean success;
    private String message;
    private List<Background> backgroundList;

    public BackgroundLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BackgroundLoadEvent(boolean success, List<Background> backgroundList) {
        this.success = success;
        this.backgroundList = backgroundList;
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

    public List<Background> getBackgroundList() {
        return backgroundList;
    }

    public void setBackgroundList(List<Background> backgroundList) {
        this.backgroundList = backgroundList;
    }
}
