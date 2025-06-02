package com.ocn.beans;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderBean {
    private int orderId;
    private String orderStatus;
    private String orderScheduleDate;
    private String orderScheduleTime;
    private double orderAmount;
    private String orderPaymentType;
    private int userId;
    private Integer orderFeedback;
    private String customerName;
    private boolean priority;
    private OrderDetailBean mealDetails;

    private List<OrderDetailBean> meals = new ArrayList<>();


    public OrderBean() {
    }

    public OrderBean(int orderId, String orderStatus, String orderScheduleDate, String orderScheduleTime, double orderAmount, String orderPaymentType, int userId, Integer orderFeedback) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderScheduleDate = orderScheduleDate;
        this.orderScheduleTime = orderScheduleTime;
        this.orderAmount = orderAmount;
        this.orderPaymentType = orderPaymentType;
        this.userId = userId;
        this.orderFeedback = orderFeedback;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderScheduleDate() {
        return orderScheduleDate;
    }

    public void setOrderScheduleDate(String orderScheduleDate) {
        this.orderScheduleDate = orderScheduleDate;
    }

    public String getOrderScheduleTime() {
        return orderScheduleTime;
    }

    public void setOrderScheduleTime(String orderScheduleTime) {
        this.orderScheduleTime = orderScheduleTime;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderPaymentType() {
        return orderPaymentType;
    }

    public void setOrderPaymentType(String orderPaymentType) {
        this.orderPaymentType = orderPaymentType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getOrderFeedback() {
        return orderFeedback;
    }

    public void setOrderFeedback(Integer orderFeedback) {
        this.orderFeedback = orderFeedback;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public List<OrderDetailBean> getMeals() {
        return meals;
    }

    public void setMeals(List<OrderDetailBean> meals) {
        this.meals = meals;
    }


    public OrderDetailBean getMealDetails() {
        return mealDetails;
    }

    public void setMealDetails(OrderDetailBean mealDetails) {
        this.mealDetails = mealDetails;
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = (Date) dbFormat.parse(orderScheduleDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy");
            return displayFormat.format(date);
        } catch (Exception e) {
            return orderScheduleDate;
        }
    }

    public String getFormattedTime() {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("hh:mm a");
            java.util.Date time = dbFormat.parse(orderScheduleTime); // Explicit java.util.Date
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            return displayFormat.format(time);
        } catch (Exception e) {
            return orderScheduleTime;
        }
    }


    @Override
    public String toString() {
        return "OrderBean{" +
                "orderId=" + orderId +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderScheduleDate='" + orderScheduleDate + '\'' +
                ", orderScheduleTime='" + orderScheduleTime + '\'' +
                ", orderAmount=" + orderAmount +
                ", orderPaymentType='" + orderPaymentType + '\'' +
                ", userId=" + userId +
                ", orderFeedback=" + orderFeedback +
                ", customerName='" + customerName + '\'' +
                ", priority=" + priority +
                ", mealDetails=" + mealDetails +
                ", meals=" + meals +
                '}';
    }
}

