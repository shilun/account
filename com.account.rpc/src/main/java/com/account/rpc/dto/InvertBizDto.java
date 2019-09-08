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
     * 币种
     *  1(保险箱金币)    2 现金
     */
    private Integer tokenType;
    /**
     * 系统类型 1 订单 3 棋牌 4 彩票 5小游戏 6签到系统
    */
    private Integer bizType;

    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
    /**
     * 总额数
     */
    private BigDecimal amount;

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

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
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
}
