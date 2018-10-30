package com.account.domain;

import com.common.util.AbstractBaseEntity;

import java.math.BigDecimal;
import java.util.Date;

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
     * 系统类型 1 订单 3 棋牌 4 彩票 5小游戏
     */
    private Integer bizType;
    /**
     * 业务类型 充值 1 提款 2 赠送 3
     */
    private Integer bizToken;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;

    /***
     * 币种
     * * 1(人民币)    2 金币   3 农场币  4彩票币
     */
    private Integer tokenType;
    /**
     * 充值方式 0 数据库默认值  1 支付宝 2 微信 3 银联
     */
    private Integer chargeType;
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

    /**
     * 是否机器人 1 是 2不是
     */
    private Integer isRobot;
    /**
     * 时间筛选查询  1 当天  2 本周 3 本月
     */
    private Integer dayStatus;

    public Integer getDayStatus() {
        return dayStatus;
    }

    public void setDayStatus(Integer dayStatus) {
        this.dayStatus = dayStatus;
    }

    public Integer getIsRobot() {
        return isRobot;
    }

    public void setIsRobot(Integer isRobot) {
        this.isRobot = isRobot;
    }

    public Integer getBizToken() {
        return bizToken;
    }

    public void setBizToken(Integer bizToken) {
        this.bizToken = bizToken;
    }


    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
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
