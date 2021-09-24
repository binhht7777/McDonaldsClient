package com.example.mcdonalds.Model;

public class User {
    private String imei, userphone, name, address, iscustomeryn;

    public User(String imei, String userphone, String name, String address, String iscustomeryn) {
        this.imei = imei;
        this.userphone = userphone;
        this.name = name;
        this.address = address;
        this.iscustomeryn = iscustomeryn;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIscustomeryn() {
        return iscustomeryn;
    }

    public void setIscustomeryn(String iscustomeryn) {
        this.iscustomeryn = iscustomeryn;
    }
}
