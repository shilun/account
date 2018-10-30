package com.account.web.controller.dto;

import com.common.util.AbstractDTO;

public class PayInfoDto extends AbstractDTO {
    /**
     * 是否已支付 1 是 2 否
     */
    private Integer payStatus;

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }
}
