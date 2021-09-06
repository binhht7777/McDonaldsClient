package com.example.mcdonalds.Retrofit;



import com.example.mcdonalds.Model.BackgroundModel;
import com.example.mcdonalds.Model.FavoriteModel;
import com.example.mcdonalds.Model.FoodModel;
import com.example.mcdonalds.Model.Store;
import com.example.mcdonalds.Model.StoreModel;
import com.example.mcdonalds.Model.UpdateUserModel;
import com.example.mcdonalds.Model.UserModel;
import com.example.mcdonalds.Model.CategorydModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMcDonaldsAPI {


    //lay thong tin background
    @GET("users")
    Observable<UserModel> getUsers(@Query("key") String apiKey,
                                   @Query("userPhone") String userPhone);

    //lay thong tin background
    @GET("background")
    Observable<BackgroundModel> getBackground(@Query("key") String apiKey);

    // Get Category
    @GET("category")
    Observable<CategorydModel> getCategory(@Query("key") String apiKey);

    // Get food by category
    @GET("foodByCategory")
    Observable<FoodModel> getFoodByCategory(@Query("key") String apiKey,
                                            @Query("categoryId") String categoryId);

    // ***** METHOD POST
    @POST("users")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUses(@Field("key") String apiKey,
                                           @Field("userId") String userId,
                                           @Field("userPhone") String userPhone,
                                           @Field("name") String name);


    // Get Category
    @GET("favorite")
    Observable<FavoriteModel> getFavorite(@Query("key") String apiKey);

    @GET("store")
    Observable<StoreModel> getStore(@Query("key") String apiKey);

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
