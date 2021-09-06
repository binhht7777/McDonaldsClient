package com.example.mcdonalds.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String userPhone, String foodId) {
        return cartDAO.getAllCart(userPhone, foodId);
    }

    @Override
    public Flowable<List<CartItem>> getAllCart2(String userPhone) {
        return cartDAO.getAllCart2(userPhone);
    }

    @Override
    public Single<Integer> countCart(String userPhone) {
        return cartDAO.countCart(userPhone);
    }

    @Override
    public Single<Integer> countCart2(String userPhone, String foodId) {
        return cartDAO.countCart2(userPhone, foodId);
    }

    @Override
    public Single<Long> sumPrice(String userPhone, String foodId) {
        return cartDAO.sumPrice(userPhone, foodId);
    }

    @Override
    public Single<Long> sumPrice2(String userPhone) {
        return cartDAO.sumPrice2(userPhone);
    }

    @Override
    public Single<CartItem> getItemInCart(String userPhone, String foodId, String categoryId) {
        return cartDAO.getItemInCart(foodId, userPhone, categoryId);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCart(CartItem cart) {
        return cartDAO.updateCart(cart);
    }

    @Override
    public Single<Integer> deleteCart(CartItem cart) {
        return cartDAO.deleteCart(cart);
    }

    @Override
    public Single<Integer> cleanCart(String userPhone) {
        return cartDAO.cleanCart(userPhone);
    }
}
