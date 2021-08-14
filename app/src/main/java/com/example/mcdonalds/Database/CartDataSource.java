package com.example.mcdonalds.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {
    Flowable<List<CartItem>> getAllCart(String userPhone, String foodId);

    Single<Integer> countCart(String userPhone);
    Single<Integer> countCart2(String userPhone, String foodId);

    Single<Long> sumPrice(String userPhone, String foodId);

    Single<CartItem> getItemInCart(String foodId, String userPhone, String categoryId);

    Completable insertOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCart(CartItem cart);

    Single<Integer> deleteCart(CartItem cart);

    Single<Integer> cleanCart(String userPhone, String foodId);
}
