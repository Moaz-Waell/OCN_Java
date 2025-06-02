package com.ocn.beans;

public class CartBean {
    private int cartId;
    private int userId;
    private int mealId;
    private String note;
    private int quantity;


    public CartBean() {
    }

    public CartBean(int cartId, int userId, int mealId, String note, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.mealId = mealId;
        this.note = note;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

