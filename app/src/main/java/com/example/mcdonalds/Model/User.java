package com.example.mcdonalds.Model;

public class User {
    private String UserId, UserPhone, Name;

    public User(String userId, String userPhone, String name) {
        UserId = userId;
        UserPhone = userPhone;
        Name = name;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
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
}
