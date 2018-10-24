package com.account.domain.module;

import com.common.util.IGlossary;

/**
 * 充值类型
 */
public enum ChargeTypeEnum implements IGlossary {
    /**
     * 数据库默认0
     */
    moren("其他消费",0),
    /**
     * 支付宝
     */
    zhifubao("支付宝",1),
    /**
     * 微信
     */
    weixin("微信",2),
    /**
     * 银联
     */
    yinlian("银联",3),
    ;

    ChargeTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private Integer value;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
