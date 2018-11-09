package com.account.rpc.dto;

import java.io.Serializable;

public class UserBankDto implements Serializable {
    /**
     * 代理id
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
     * 银行卡类型
     */
    private Integer bankType;
    /**
     * 银行卡图片
     */
    private String img;
    /**
     * 账户
     */
    private String code;

    /**
     * 开户行
     */
    private String bankName;
    /**
     * 真实姓名
     */
    private String name;

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

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public Integer getBankType() {
        return bankType;
    }

    public void setBankType(Integer bankType) {
        this.bankType = bankType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
