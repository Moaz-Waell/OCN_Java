package com.ocn.beans;
public class OrderDetailBean {
    private int orderId;
    private int mealId;
    private int quantity;
    private String note;
    private String mealName;       // Add this field
    private String mealDescription; // Add this field
    private MealBean meal; // Add this field

    public OrderDetailBean() {
    }

    public OrderDetailBean( int orderId, int mealId, int quantity, String note) {

        this.orderId = orderId;
        this.mealId = mealId;
        this.quantity = quantity;
        this.note = note;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

     public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    // Getter and Setter
    public MealBean getMeal() { return meal; }
    public void setMeal(MealBean meal) { this.meal = meal; }


    @Override
    public String toString() {
        return "OrderDetailBean{" +
                "orderId=" + orderId +
                ", mealId=" + mealId +
                ", quantity=" + quantity +
                ", note='" + note + '\'' +
                ", mealName='" + mealName + '\'' +
                ", mealDescription='" + mealDescription + '\'' +
                ", meal=" + meal +
                '}';
    }
}
