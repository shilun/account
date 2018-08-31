package com.account.domain.module;

import com.common.util.IGlossary;

/**
 * 账户明细业务状态
 */
public enum  DetailStatusEnum implements IGlossary {
    Normal("正常",1),
    Rollback("回滚",2)
    ;

    DetailStatusEnum(String name, Integer value) {
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
