package com.example.mcdonalds.Common;

import com.example.mcdonalds.Database.CartItem;
import com.example.mcdonalds.Model.Food;
import com.example.mcdonalds.Model.Table;
import com.example.mcdonalds.Model.User;
import com.example.mcdonalds.Model.Category;

import java.util.List;

public class Common {
    public static String API_RESTAURANT_ENDPOINT = "https://obscure-hamlet-06195.herokuapp.com/";
    public static final String API_KEY = "1234";

    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static String globalFoodId = "";
    public static Category currentCategory;
    public static Table currentTable;
    public static Food currentFood;
    public static User currentUser;
    public static String isCustomerYN;
    public static Float totalCash;
    public static String Imei;
    public static final String currency = "VND";
    public static List<CartItem> myCartItemList;
}
