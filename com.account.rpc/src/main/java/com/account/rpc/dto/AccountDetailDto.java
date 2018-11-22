package com.account.rpc.dto;

import com.common.util.AbstractDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
     * 用户id
     */
    private Long userCode;

    /**
     * 代币类型
     */
    private String tokenName;
    /**
     * 系统类型 1 订单 3 棋牌 4 彩票 5小游戏 6 签到系统
     */
    private Integer bizType;
    /**
     * 业务类型 充值 1 提款 2 赠送 3 消费 4 棋牌扣费 5
     */
    private Integer bizToken;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
    /**
     * 业务类型名称
     */
    private String bizTypeName;

    /***
     * 账本类型 1 人民币 2 金币 3 农场币  4 彩票币
     */
    private Integer tokenType;
    /**
     * 账本类型名称
     */
    private String tokenTypeName;

    /**
     * 充值方式 0 数据库默认值  1 支付宝 2 微信 3 银联
     */
    private Integer chargeType;
    /**
     * 充值方式名称
     */
    private String chargeTypeName;
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 查询起始时间
     */
    private String queryStartTime;
    /**
     * 查询结束时间
     */
    private String queryEndTime;

    /**
     * 是否机器人 1 是 2不是
     */
    private Integer isRobot;
    /**
     * 时间筛选查询  1 全部 2 当天  3 本周 4 本月
     */
    private Integer dayStatus;

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

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

    public String getChargeTypeName() {
        return chargeTypeName;
    }

    public void setChargeTypeName(String chargeTypeName) {
        this.chargeTypeName = chargeTypeName;
    }

    public String getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(String queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public String getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(String queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizTypeName() {
        return bizTypeName;
    }

    public void setBizTypeName(String bizTypeName) {
        this.bizTypeName = bizTypeName;
    }

    public String getTokenTypeName() {
        return tokenTypeName;
    }

    public void setTokenTypeName(String tokenTypeName) {
        this.tokenTypeName = tokenTypeName;
    }

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

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
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
