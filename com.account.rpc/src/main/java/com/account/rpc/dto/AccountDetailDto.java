package com.account.rpc.dto;

import com.common.util.AbstractDTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountDetailDto extends AbstractDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 代理商id
     */
    private Long proxyId;
    /**
     * pin
     */
    private String pin;

    /**
     * 代币类型
     */
    private String tokenName;
    /**
     * 业务类型 1 充值 2 体现 3 棋牌 4 彩票 5小游戏
     */
    private Integer bizType;

    /***
     * 账本类型 1 人民币 2 金币 3 测试账户  4 测试金币
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


    public Long getProxyId() {
        return proxyId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
