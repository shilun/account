package com.account.domain;

import com.common.util.AbstractBaseEntity;

import java.math.BigDecimal;

/**
 * @desc 账本流水账
 */
public class AccountDetail extends AbstractBaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * pin
     */
    private String pin;

    /**
     * 代理商id
     */
    private Long proxyId;

    /**
     * 代币类型
     */
    private String tokenName;
    /**
     * 业务类型 1 棋牌 2 彩票 3小游戏
     */
    private Integer bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;

    /***
     * 账本类型
     */
    private String tokenType;
    /**
     * before_总余额
     */
    private BigDecimal beforeAmount;
    /**
     * before_冻结余额
     */
    private BigDecimal beforeFreeze;
    /**
     * change_总余额
     */
    private BigDecimal changeAmount;
    /**
     * change_冻结余额
     */
    private BigDecimal changeFreeze;
    /**
     * after_总余额
     */
    private BigDecimal afterAmount;
    /**
     * after_冻结余额
     */
    private BigDecimal afterFreeze;

    /**
     * 1 正常 2 回滚
     */
    private Integer status;


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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
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

    public BigDecimal getBeforeAmount() {
        return beforeAmount;
    }

    public void setBeforeAmount(BigDecimal beforeAmount) {
        this.beforeAmount = beforeAmount;
    }

    public BigDecimal getBeforeFreeze() {
        return beforeFreeze;
    }

    public void setBeforeFreeze(BigDecimal beforeFreeze) {
        this.beforeFreeze = beforeFreeze;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getChangeFreeze() {
        return changeFreeze;
    }

    public void setChangeFreeze(BigDecimal changeFreeze) {
        this.changeFreeze = changeFreeze;
    }

    public BigDecimal getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(BigDecimal afterAmount) {
        this.afterAmount = afterAmount;
    }

    public BigDecimal getAfterFreeze() {
        return afterFreeze;
    }

    public void setAfterFreeze(BigDecimal afterFreeze) {
        this.afterFreeze = afterFreeze;
    }
}
