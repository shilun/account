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
     * 币种
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
    private Integer testStatus;

    public Integer getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Integer testStatus) {
        this.testStatus = testStatus;
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
