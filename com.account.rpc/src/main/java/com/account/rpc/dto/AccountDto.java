package com.account.rpc.dto;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账本信息
 */
public class AccountDto implements Serializable {
    private Long proxyId;
    /**
     * pin
     */
    private String pin;

    /**
     * 用户id
     */
    private Long userCode;
    /**
     * 币种
     * 1(保险箱金币)    2 现金
     */
    private Integer tokenType;
    /**
     * 余额
     */
    private BigDecimal amount;
    /**
     * 冻结
     */
    private BigDecimal freeze;
    /**
     * 是否为测试 1 否 2 是
     */
    private Integer test;

    /**
     * 汇率
     */
    private BigDecimal rate;

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFreeze() {
        return freeze;
    }

    public void setFreeze(BigDecimal freeze) {
        this.freeze = freeze;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }
}
