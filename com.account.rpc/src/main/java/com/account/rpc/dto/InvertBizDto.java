package com.account.rpc.dto;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 业务实体
 *
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
    private Integer tokenType;
    /**
     * 业务类型
     */
    private Integer bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
    /**
     * 可用余额数
     */
    private BigDecimal amount;
    /**
     * 冻结数
     */
    private BigDecimal freeze;

    /**
     * 是否为测试
     */
    private Integer test;

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
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


    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
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
