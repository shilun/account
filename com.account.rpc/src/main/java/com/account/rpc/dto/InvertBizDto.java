package com.account.rpc.dto;

import java.io.Serializable;

/**
 * 业务实体
 */
public class InvertBizDto implements Serializable {
    private String pin;
    /**
     * 代币类型
     */
    private String tokenName;
    /**
     * 1 付款 2 收款 3 充值 4 提币
     */
    private BizTypeEnum bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
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

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public BizTypeEnum getBizType() {
        return bizType;
    }

    public void setBizType(BizTypeEnum bizType) {
        this.bizType = bizType;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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
