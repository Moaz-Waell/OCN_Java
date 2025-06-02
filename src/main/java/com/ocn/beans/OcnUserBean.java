package com.ocn.beans;

public class OcnUserBean {
    private int userId;
    private String userName;
    private String userPhone;
    private int userAttendance;
    private String userEmail;

    public OcnUserBean() {
    }

    public OcnUserBean(int userId, String userName, int userPin, String userPhone, int userAttendance , String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAttendance = userAttendance;
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getUserAttendance() {
        return userAttendance;
    }

    public void setUserAttendance(int userAttendance) {
        this.userAttendance = userAttendance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "OcnUserBean{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userAttendance=" + userAttendance +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
