package com.ocn.beans;

public class UniUserBean {
    private String uniId;
    private String uniPin;
    private String uniName;
    private String uniPhone;
    private int uniAttendance;
    private String uniEmail;

    public UniUserBean() {

    }

    public UniUserBean(String uniId, String uniName, String uniPin, int uniAttendance, String uniPhone , String uniEmail) {
        this.uniId = uniId;
        this.uniPin = uniPin;
        this.uniName = uniName;
        this.uniPhone = uniPhone;
        this.uniAttendance = uniAttendance;
        this.uniEmail = uniEmail;
    }

    public int getUniAttendance() {
        return uniAttendance;
    }

    public void setUniAttendance(int uniAttendance) {
        this.uniAttendance = uniAttendance;
    }

    public String getUniPhone() {
        return uniPhone;
    }

    public void setUniPhone(String uniPhone) {
        this.uniPhone = uniPhone;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public String getUniPin() {
        return uniPin;
    }

    public void setUniPin(String uniPin) {
        this.uniPin = uniPin;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    public String getUniEmail() {
        return uniEmail;
    }

    public void setUniEmail(String uniEmail) {
        this.uniEmail = uniEmail;
    }

    @Override
    public String toString() {
        return "UniUserBean{" +
                "uniId='" + uniId + '\'' +
                ", uniPin='" + uniPin + '\'' +
                ", uniName='" + uniName + '\'' +
                ", uniPhone='" + uniPhone + '\'' +
                ", uniAttendance=" + uniAttendance +
                ", uniEmail='" + uniEmail + '\'' +
                '}';
    }
}

