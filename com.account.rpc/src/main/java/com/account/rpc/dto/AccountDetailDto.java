package com.account.rpc.dto;

import com.common.util.AbstractDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountDetailDto extends AbstractDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * pin
     */
    private String pin;

    /**
     * 业务标识（外键流水号）
     */
    private String bizId;

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
}
