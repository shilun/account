package com.account.domain;

import com.common.util.AbstractBaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

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
