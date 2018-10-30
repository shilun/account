package com.account.web.controller.dto;

import com.common.util.AbstractDTO;

public class PayInfoOrderDto extends AbstractDTO {
    /**
     * 是否已支付 1 是 2 否
     */
    private Integer payStatus;

    /**
     * 支付商品id
     */
    private Integer productId;


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }
}
