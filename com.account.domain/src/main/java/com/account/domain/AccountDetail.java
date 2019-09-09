package com.account.domain;

import com.common.util.AbstractBaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @desc 账本流水账
 */
@Data
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
     * 业务标识（外键流水号）
     */
    private String bizId;

    /***
     * 币种
     * * 1(人民币)    2 金币   3 农场币  4彩票币
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
     * 备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String operator;



}
