package com.account.web.controller.dto;

import java.io.Serializable;

public class LoginDto implements Serializable {
    private String account;
    private String code;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
