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
     * 1(保险箱金币)    2 现金
     */
    private Integer tokenType;
    /**
     * 余额
     */
    private BigDecimal amount;

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


    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
