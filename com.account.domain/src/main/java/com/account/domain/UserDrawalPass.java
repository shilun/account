package com.account.domain;

import com.common.util.AbstractBaseEntity;

/**
 * 用户提款密码
 */
public class UserDrawalPass extends AbstractBaseEntity {
    /**
     * 代理商
     */
    private Long proxyId;
    /**
     * 用户pin
     */
    private String pin;
    /**
     * 用户id
     */
    private Long userCode;
    /**
     * 密码
     */
    private String pass;

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
