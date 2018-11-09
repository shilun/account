package com.account.domain;

import com.common.annotation.QueryField;
import com.common.mongo.QueryType;
import com.common.util.AbstractBaseEntity;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提款代理商提款记录
 */
public class UserDrawalLog extends AbstractBaseEntity {

    /**
     * 代理商id
     */
    private Long proxyId;

    /***
     * 审核状态 1通过 2不通过 3待审核
     */
    private Integer audiStatus;

    /**
     * 提款人
     */
    private String pin;
    /**
     * 用户id
     */
    private Long userCode;

    /**
     * 审核时间
     */
    private Date audiTime;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 提款状态
     */
    private Integer status;

    /**
     * 提款日期
     */
    private Date drawingDate;

    /**
     * 扣款状态
     */
    private Integer detainStatus;

    /**
     * 起始提款时间
     */
    @Transient
    @QueryField(name="createTime",type= QueryType.GTE)
    private Date startDrawingDate;

    /**
     * 结束提款时间
     */
    @Transient
    @QueryField(name="createTime",type= QueryType.LT)
    private Date endDrawingDate;

    /**
     * 起始审核时间
     */
    @Transient
    @QueryField(name="audiTime",type= QueryType.GTE)
    private Date startAudiTime;

    /**
     * 结束审核时间
     */
    @Transient
    @QueryField(name="audiTime",type= QueryType.LT)
    private Date endAudiTime;

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public Date getStartDrawingDate() {
        return startDrawingDate;
    }

    public void setStartDrawingDate(Date startDrawingDate) {
        this.startDrawingDate = startDrawingDate;
    }

    public Date getEndDrawingDate() {
        return endDrawingDate;
    }

    public void setEndDrawingDate(Date endDrawingDate) {
        this.endDrawingDate = endDrawingDate;
    }

    public Date getStartAudiTime() {
        return startAudiTime;
    }

    public void setStartAudiTime(Date startAudiTime) {
        this.startAudiTime = startAudiTime;
    }

    public Date getEndAudiTime() {
        return endAudiTime;
    }

    public void setEndAudiTime(Date endAudiTime) {
        this.endAudiTime = endAudiTime;
    }

    public Integer getDetainStatus() {
        return detainStatus;
    }

    public void setDetainStatus(Integer detainStatus) {
        this.detainStatus = detainStatus;
    }

    public Integer getAudiStatus() {
        return audiStatus;
    }

    public void setAudiStatus(Integer audiStatus) {
        this.audiStatus = audiStatus;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Date getDrawingDate() {
        return drawingDate;
    }

    public void setDrawingDate(Date drawingDate) {
        this.drawingDate = drawingDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAudiTime() {
        return audiTime;
    }

    public void setAudiTime(Date audiTime) {
        this.audiTime = audiTime;
    }

    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
