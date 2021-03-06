package com.example.mcdonalds.Common;

import com.example.mcdonalds.Database.CartItem;
import com.example.mcdonalds.Model.Favorite;
import com.example.mcdonalds.Model.FavoriteOnlyId;
import com.example.mcdonalds.Model.Food;
import com.example.mcdonalds.Model.Table;
import com.example.mcdonalds.Model.User;
import com.example.mcdonalds.Model.Category;

import java.util.List;

public class Common {
    public static String API_RESTAURANT_ENDPOINT = "https://obscure-hamlet-06195.herokuapp.com/";
    //        public static String API_RESTAURANT_ENDPOINT = "http://192.168.1.6:3000/";
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
    public static List<FavoriteOnlyId> currentFavoriteFoodALL;
    public static List<FavoriteOnlyId> currentFavoriteFood;
    public static List<Favorite> getFavoriteAll;
    public static String sumFood;

    public static boolean checkFavorite(String id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavoriteFoodALL) {
            if (item.getFoodid().compareTo(id) == 0) {
                result = true;
            }
        }
        return result;
    }

    public static void removeFavorite(String id) {
        for (FavoriteOnlyId item : currentFavoriteFoodALL) {
            if (item.getFoodid().compareTo(id) == 0) {
                currentFavoriteFoodALL.remove(item);
            }
        }
    }

    public static boolean checkFavorite1(String id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavoriteFood) {
            if (item.getFoodid().compareTo(id) == 0) {
                result = true;
            }
        }
        return result;
    }

    public static boolean checkFavoriteSum(String id, String event) {
        sumFood = "";
        boolean result = false;
        for (Favorite item : getFavoriteAll) {
            if (item.getFoodid().compareTo(id) == 0) {
                if (event == "ADD") {
                    sumFood = String.valueOf(item.getCount() + 1);
                } else if (event == "DEL") {
                    sumFood = String.valueOf(item.getCount());
                } else {
                    sumFood = String.valueOf(item.getCount());
                }
                result = true;
            }

        }
        return result;
    }

    public static void removeFavorite1(String id) {
        for (FavoriteOnlyId item : currentFavoriteFood) {
            if (item.getFoodid().compareTo(id) == 0) {
                currentFavoriteFood.remove(item);
            }
        }
    }
}
