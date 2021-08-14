package com.example.mcdonalds.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {
    @Query("Select * From Cart Where userPhone=:userPhone And foodId=:foodId")
    Flowable<List<CartItem>> getAllCart(String userPhone, String foodId);

    @Query("Select Count(*) From Cart Where userPhone=:userPhone")
    Single<Integer> countCart(String userPhone);

    @Query("Select Count(*) From Cart Where userPhone=:userPhone And foodId=:foodId")
    Single<Integer> countCart2(String userPhone, String foodId);

    @Query("Select SUM(foodPrice*foodQuantity) From Cart Where userPhone=:userPhone And foodId=:foodId")
    Single<Long> sumPrice(String userPhone, String foodId);

    @Query("Select * From Cart Where foodId=:foodId And userPhone=:userPhone And categoryId=:categoryId")
    Single<CartItem> getItemInCart(String foodId, String userPhone, String categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCart(CartItem cart);

    @Delete
    Single<Integer> deleteCart(CartItem cart);

    @Query("Delete From Cart Where userPhone=:userPhone And foodId=:foodId")
    Single<Integer> cleanCart(String userPhone, String foodId);
}
