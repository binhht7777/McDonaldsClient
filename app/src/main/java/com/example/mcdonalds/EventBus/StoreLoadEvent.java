package com.example.mcdonalds.EventBus;

import com.example.mcdonalds.Model.Store;

import java.util.List;

public class StoreLoadEvent {
    private boolean success;
    private List<Store> storeList;

    public StoreLoadEvent(boolean success, List<Store> storeList) {
        this.success = success;
        this.storeList = storeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
    }
}
