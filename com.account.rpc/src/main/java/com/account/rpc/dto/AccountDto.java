package com.account.rpc.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账本信息
 */
@Data
public class AccountDto implements Serializable {
    /**
     * pin
     */
    private String pin;

    /**
     * 余额
     */
    private BigDecimal amount;
}
