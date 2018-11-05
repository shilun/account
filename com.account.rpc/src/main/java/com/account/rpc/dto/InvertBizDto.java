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
    /**
     * 充值方式 0 数据库默认值  1 支付宝 2 微信 3 银联
     */
    private Integer chargeType;
    /**
     * 总额数
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
