package com.ocn.beans;

public class AdminBean {
    private String adminId;
    private String adminName;
    private String adminPin;

    public AdminBean() {
    }

    public AdminBean(String adminId, String adminName, String adminPin) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminPin = adminPin;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPin() {
        return adminPin;
    }

    public void setAdminPin(String adminPin) {
        this.adminPin = adminPin;
    }

    @Override
    public String toString() {
        return "AdminBean{" +
                "adminId='" + adminId + '\'' +
                ", adminName='" + adminName + '\'' +
                ", adminPin='" + adminPin + '\'' +
                '}';
    }
}

