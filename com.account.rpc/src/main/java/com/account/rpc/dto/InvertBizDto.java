package com.account.rpc.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 业务实体
 *
 */
@Data
public class InvertBizDto implements Serializable {
    private String pin;
    /**
     * 账户类型
     */
    private Integer tokenType;
    /**
     * 业务类型
     */
    private BizTypeEnum bizType;
    /**
     * 业务标识（外键流水号）
     */
    private String bizId;
    /**
     * 总额数
     */
    private BigDecimal amount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 操作人
     */
    private String operator;
}
