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
     * 系统类型 1 订单 3 棋牌 4 彩票 5小游戏 6 签到系统
     */
    private Integer bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;

    /***
     * 账本类型 1 人民币 2 金币 3 农场币  4 彩票币
     */
    private Integer tokenType;

    /**
     * before_总余额
     */
    private BigDecimal beforeAmount;
    /**
     * change_总余额
     */
    private BigDecimal changeAmount;
    /**
     * after_总余额
     */
    private BigDecimal afterAmount;

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

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(BigDecimal afterAmount) {
        this.afterAmount = afterAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
}
