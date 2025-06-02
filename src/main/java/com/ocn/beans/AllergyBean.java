package com.ocn.beans;

public class AllergyBean {
    private int allergyId;
    private String allergyName;

    public AllergyBean() {
    }

    public AllergyBean(int allergyId, String allergyName) {
        this.allergyId = allergyId;
        this.allergyName = allergyName;
    }

    public int getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(int allergyId) {
        this.allergyId = allergyId;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    @Override
    public String toString() {
        return "AllergyBean{" +
                "allergyId=" + allergyId +
                ", allergyName='" + allergyName + '\'' +
                '}';
    }
}

