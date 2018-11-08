package com.account.domain.module;

import com.common.util.IGlossary;

public enum BizTokenEnum implements IGlossary {
    /**
     * 充值
     */
    recharge("充值",1),
    /**
     * 提款
     */
    drawing("提款",2),
    /**
     * 赠送金币
     */
    givecoin("赠送金币",3),
    /**
     * 消费
     */
    consume("消费",4),

    qipaiconsume("棋牌扣费",5)
    ;

    BizTokenEnum(String name, Integer value) {
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
