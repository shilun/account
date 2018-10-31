package com.account.domain;

import com.common.util.AbstractBaseEntity;

import java.math.BigDecimal;

/**
 * 提现配置信息
 */
public class WithdrawCfgInfo extends AbstractBaseEntity {
    /**
     * 代理商ID
     */
    private Long proxyId;
    /**
     * 最小提现金额
     */
    private BigDecimal minMoney;
    /**
     * 最大提现金额
     */
    private BigDecimal maxMoney;
    /*
     * 最早提现时间
     */
    private Integer minTime;
    /**
     * 最晚提现时间
     */
    private Integer maxTime;

    /**
     * 是否允许提现
     */
    private Integer status;


    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public BigDecimal getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(BigDecimal minMoney) {
        this.minMoney = minMoney;
    }

    public BigDecimal getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(BigDecimal maxMoney) {
        this.maxMoney = maxMoney;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMinTime() {
        return minTime;
    }

    public void setMinTime(Integer minTime) {
        this.minTime = minTime;
    }

    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }
}
