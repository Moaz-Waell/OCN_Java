package com.ocn.beans;

public class MealBean {
    private int mealId;
    private String mealName;
    private String mealDescription;
    private double mealPrice;
    private String mealIcon;
    private int categoryId;
    private String categoryName;


    public MealBean() {
    }

    public MealBean(int mealId, String mealName, String mealDescription, double mealPrice, String mealIcon, int categoryId) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealDescription = mealDescription;
        this.mealPrice = mealPrice;
        this.mealIcon = mealIcon;
        this.categoryId = categoryId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(double mealPrice) {
        this.mealPrice = mealPrice;
    }

    public String getMealIcon() {
        return mealIcon;
    }

    public void setMealIcon(String mealIcon) {
        this.mealIcon = mealIcon;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "MealBean{" +
                "mealId=" + mealId +
                ", mealName='" + mealName + '\'' +
                ", mealDescription='" + mealDescription + '\'' +
                ", mealPrice=" + mealPrice +
                ", mealIcon='" + mealIcon + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}

