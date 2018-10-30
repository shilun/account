package com.account.web.controller.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserDrawalDto  implements Serializable {
    /**
     * 提现验证码
     */
    private String msg;
    /**
     * 金额
     */
    private BigDecimal amount;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
