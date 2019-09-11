package com.account.domain;

import com.common.util.AbstractBaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * @desc 账户信息 account
 */
@Data
public class Account extends AbstractBaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 代理商id
     */
    private Long proxyId;
    /**
     * pin
     */
    private String pin;
    /***币种 1(人民币)    2 金币*/
    private Integer tokenType;
    /**
     * 总金额
     */
    private BigDecimal amount;
    /**
     * 状态 1 启用 2 禁用
     */
    private Integer status;
    /**
     * 版本
     */
    private Integer ver;

}
