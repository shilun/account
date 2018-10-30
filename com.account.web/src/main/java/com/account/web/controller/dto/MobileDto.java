package com.account.web.controller.dto;

import java.io.Serializable;

public class MobileDto implements Serializable {
    private String phoneNo;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
