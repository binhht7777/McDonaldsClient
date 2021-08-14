package com.example.mcdonalds.Model;

import java.util.List;

public class BackgroundModel {
    private boolean success;
    private String message;
    private List<Background> result;

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

    public List<com.example.mcdonalds.Model.Background> getResult() {
        return result;
    }

    public void setResult(List<com.example.mcdonalds.Model.Background> result) {
        this.result = result;
    }
}
