package com.example.mcdonalds.Model;

public class User {
    private String imei, userphone, name, iscustomeryn;

    public User(String imei, String userPhone, String name, String isCustomerYN) {
        imei = imei;
        userphone = userPhone;
        name = name;
        iscustomeryn = isCustomerYN;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        imei = imei;
    }

    public String getUserPhone() {
        return userphone;
    }

    public void setUserPhone(String userPhone) {
        userphone = userPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getIsCustomerYN() {
        return iscustomeryn;
    }

    public void setIsCustomerYN(String isCustomerYN) {
        iscustomeryn = isCustomerYN;
    }
}
