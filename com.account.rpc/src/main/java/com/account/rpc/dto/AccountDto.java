package com.account.rpc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
    private BigDecimal amount;
    /**
     * 冻结
     */
    private BigDecimal freeze;

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
}
