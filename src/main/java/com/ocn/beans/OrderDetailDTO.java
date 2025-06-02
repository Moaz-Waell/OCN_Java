package com.ocn.beans;

public class OrderDetailDTO {
    private OrderDetailBean orderDetail;
    private MealBean meal;

    public OrderDetailDTO(OrderDetailBean orderDetail, MealBean meal) {
        this.orderDetail = orderDetail;
        this.meal = meal;
    }

    // Getters
    public OrderDetailBean getOrderDetail() { return orderDetail; }
    public MealBean getMeal() { return meal; }
}