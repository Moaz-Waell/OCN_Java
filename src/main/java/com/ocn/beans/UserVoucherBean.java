package com.ocn.beans;

import java.sql.Date;

public class UserVoucherBean {
    private int userVoucherId;
    private int userId;
    private int voucherId;
    private Date startDate;
    private Date endDate;
    private double percentage;    // New field
    private String voucherType;   // New field

    // Add these getters/setters
    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    // Existing getters/setters below
    public int getUserVoucherId() { return userVoucherId; }
    public void setUserVoucherId(int userVoucherId) { this.userVoucherId = userVoucherId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVoucherId() { return voucherId; }
    public void setVoucherId(int voucherId) { this.voucherId = voucherId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}