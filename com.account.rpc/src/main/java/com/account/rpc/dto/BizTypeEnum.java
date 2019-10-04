package com.account.rpc.dto;

import com.common.util.IGlossary;

/**
 * 系统类型
 */
public enum BizTypeEnum implements IGlossary {
    /**
     * 充值
     */
    pay("充值",1),
    /**
     * 提款
     */
    tiguan("提款",3),

    ;

    BizTypeEnum(String name, Integer value) {
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
