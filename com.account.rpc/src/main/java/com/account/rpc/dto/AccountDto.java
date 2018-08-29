package com.account.rpc.dto;

import java.io.Serializable;

/**
 * 账本信息
 */
public class AccountDto implements Serializable {
    /**
     * pin
     */
    private String pin;
    /**
     * 币种
     */
    private String tokenType;
    /**
     * 总余额
     */
    private Long amount;
    /**
     * 冻结
     */
    private Long freeze;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getFreeze() {
        return freeze;
    }

    public void setFreeze(Long freeze) {
        this.freeze = freeze;
    }

}
