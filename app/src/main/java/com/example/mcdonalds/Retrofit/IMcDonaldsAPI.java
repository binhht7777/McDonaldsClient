package com.example.mcdonalds.Retrofit;


import com.example.mcdonalds.Model.BackgroundModel;
import com.example.mcdonalds.Model.CreateOrderModel;
import com.example.mcdonalds.Model.FavoriteModel;
import com.example.mcdonalds.Model.FavoriteOnlyIdModel;
import com.example.mcdonalds.Model.FoodModel;
import com.example.mcdonalds.Model.Store;
import com.example.mcdonalds.Model.StoreModel;
import com.example.mcdonalds.Model.UpdateOrderModel;
import com.example.mcdonalds.Model.UpdateUserModel;
import com.example.mcdonalds.Model.UserModel;
import com.example.mcdonalds.Model.CategorydModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMcDonaldsAPI {


    @GET("userbyimei")
    Observable<UserModel> getImei(@Query("key") String apiKey,
                                  @Query("imei") String imei);

    //lay thong tin background
    @GET("userbyphone")
    Observable<UserModel> getUserByPhone(@Query("key") String apiKey,
                                         @Query("userPhone") String userPhone,
                                         @Query("passWord") String passWord);

    //lay thong tin background
    @GET("background")
    Observable<BackgroundModel> getBackground(@Query("key") String apiKey);

    // Get Category
    @GET("category")
    Observable<CategorydModel> getCategory(@Query("key") String apiKey);

    // Get food by category
    @GET("foodbycategory")
    Observable<FoodModel> getFoodByCategory(@Query("key") String apiKey,
                                            @Query("categoryId") String categoryId);

    // ***** METHOD POST
    @POST("users")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUses(@Field("key") String apiKey,
                                           @Field("imei") String imei,
                                           @Field("userPhone") String userPhone,
                                           @Field("name") String name);

    @POST("createorder")
    @FormUrlEncoded
    Observable<CreateOrderModel> createOrder(@Field("key") String apiKey,
                                             @Field("orderId") String orderId,
                                             @Field("imei") String imei,
                                             @Field("userPhone") String userPhone,
                                             @Field("storeId") String storeId,
                                             @Field("cash") boolean cash,
                                             @Field("total") Float total,
                                             @Field("status") String status,
                                             @Field("checkout") String checkout,
                                             @Field("address") String address);

    @POST("createorderdetail")
    @FormUrlEncoded
    Observable<UpdateOrderModel> createOrderDetail(@Field("key") String apiKey,
                                                   @Field("orderId") String orderId,
                                                   @Field("orderDetail") String orderDetail);

    @POST("users")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("imei") String imei,
                                               @Field("userPhone") String userPhone,
                                               @Field("passWord") String passWord,
                                               @Field("name") String name,
                                               @Field("address") String address,
                                               @Field("isCustomerYN") String isCustomerYN);

    // Get Category
    @GET("favorite")
    Observable<FavoriteModel> getFavorite(@Query("key") String apiKey);

    // Get Category
    @GET("allfavorite")
    Observable<FavoriteModel> getFavorite2(@Query("key") String apiKey);

    @GET("allfavorite")
    Observable<FavoriteOnlyIdModel> getAllFavorite2(@Query("key") String apiKey);

    @GET("store")
    Observable<StoreModel> getStore(@Query("key") String apiKey);

    @POST("favorite")
    @FormUrlEncoded
    Observable<FavoriteModel> insertFavorite(@Field("key") String apiKey,
                                             @Field("foodId") String foodId,
                                             @Field("foodName") String foodName,
                                             @Field("foodImg") String foodImg,
                                             @Field("count") int count);


    @POST("delfavorite")
    @FormUrlEncoded
    Observable<FavoriteModel> removeFavorite(@Field("key") String apiKey,
                                             @Field("foodId") String foodId,
                                             @Field("count") int count);

//    // GET nha hang
//    @GET("restaurant")
//    Observable<RestaurantModel> getRestaurant(@Query("key") String apiKey);
//
//    @GET("menu")
//    Observable<MenuModel> getCategory(@Query("key") String apiKey, @Query("restaurantId") int restaurantId);
//
//
//    @GET("food")
//    Observable<FoodModel> getFoodOfMenu(@Query("key") String apiKey,
//                                        @Query("menuId") int menuId);
//
//
//    @GET("foodById")
//    Observable<FoodModel> getFoodById(@Query("key") String apiKey,
//                                      @Query("foodId") int foodId);
//
//
//    @GET("size")
//    Observable<SizeModel> getSizeOfFood(@Query("key") String apiKey,
//                                        @Query("foodId") int foodId);
//
//    @GET("addon")
//    Observable<AddonModel> getAddonOfFood(@Query("key") String apiKey,
//                                          @Query("foodId") int foodId);
//
//    @GET("favorite")
//    Observable<FavoriteModel> getFavorite(@Query("key") String apiKey,
//                                          @Query("fbid") String fbid);
//
//    @GET("favoriteByRestaurant")
//    Observable<FavoriteOnlyIdModel> getFavoriteByRestaurant(@Query("key") String apiKey,
//                                                            @Query("fbid") String fbid,
//                                                            @Query("restaurantId") int restaurantId);
//
//
//    // ***** METHOD POST
//    @POST("user")
//    @FormUrlEncoded
//    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
//                                               @Field("userPhone") String userPhone,
//                                               @Field("name") String userName,
//                                               @Field("address") String userAddress,
//                                               @Field("fbid") String fbid,
//                                               @Field("password") String password);
//
//    @POST("favorite")
//    @FormUrlEncoded
//    Observable<FavoriteModel> insertFavorite(@Field("key") String apiKey,
//                                             @Field("fbid") String fbid,
//                                             @Field("foodId") int foodId,
//                                             @Field("restaurantId") int restaurantId,
//                                             @Field("restaurantName") String restaurantName,
//                                             @Field("foodName") String foodName,
//                                             @Field("foodImage") String foodImage,
//                                             @Field("price") double price);
//
//
//    @DELETE("favorite")
//    Observable<FavoriteModel> removeFavorite(@Query("key") String key,
//                                             @Query("fbid") String fbid,
//                                             @Query("foodId") int foodId,
//                                             @Query("restaurantId") int restaurantId);
}
