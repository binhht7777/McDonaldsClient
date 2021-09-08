package com.example.mcdonalds.Model;

public class User {
    private String Imei, UserPhone, Name, IsCustomerYN;

    public User(String imei, String userPhone, String name, String isCustomerYN) {
        Imei = imei;
        UserPhone = userPhone;
        Name = name;
        IsCustomerYN = isCustomerYN;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIsCustomerYN() {
        return IsCustomerYN;
    }

    public void setIsCustomerYN(String isCustomerYN) {
        IsCustomerYN = isCustomerYN;
    }
}
