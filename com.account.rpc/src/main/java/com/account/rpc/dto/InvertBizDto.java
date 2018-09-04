package com.account.rpc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 业务实体
 */
public class InvertBizDto implements Serializable {
    private String pin;
    /***
     * 代理商标识
     */
    private Long proxyId;
    /**
     * 代币类型
     */
    private TokenTypeEnum tokenType;
    /**
     * 业务类型
     */
    private BizTypeEnum bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
    /**
     * 总余额
     */
    private BigDecimal amount;
    /**
     * 冻结
     */
    private BigDecimal freeze;

    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
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
