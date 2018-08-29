package com.account.domain;

import com.common.util.AbstractBaseEntity;

/**
 * @desc 账本流水账
 */
public class AccountDetailt extends AbstractBaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * pin
     */
    private String pin;
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
    /**
     * before_总余额
     */
    private Long beforeAmount;
    /**
     * before_冻结余额
     */
    private Long beforeFreeze;
    /**
     * change_总余额
     */
    private Long changeAmount;
    /**
     * change_冻结余额
     */
    private Long changeFreeze;
    /**
     * after_总余额
     */
    private Long afterAmount;
    /**
     * after_冻结余额
     */
    private Long afterFreeze;

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

    public Long getBeforeAmount() {
        return beforeAmount;
    }

    public void setBeforeAmount(Long beforeAmount) {
        this.beforeAmount = beforeAmount;
    }

    public Long getBeforeFreeze() {
        return beforeFreeze;
    }

    public void setBeforeFreeze(Long beforeFreeze) {
        this.beforeFreeze = beforeFreeze;
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Long changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Long getChangeFreeze() {
        return changeFreeze;
    }

    public void setChangeFreeze(Long changeFreeze) {
        this.changeFreeze = changeFreeze;
    }

    public Long getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(Long afterAmount) {
        this.afterAmount = afterAmount;
    }

    public Long getAfterFreeze() {
        return afterFreeze;
    }

    public void setAfterFreeze(Long afterFreeze) {
        this.afterFreeze = afterFreeze;
    }
}
