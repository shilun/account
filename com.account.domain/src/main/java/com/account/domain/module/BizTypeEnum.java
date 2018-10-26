package com.account.domain.module;

import com.common.util.IGlossary;

/**
 * 业务类型
 */
public enum BizTypeEnum implements IGlossary {

    /**
     * 棋牌
     */
    qipai("棋牌",3),
    /**
     * 彩票
     */
    caipiao("彩票",4),
    /**
     * 小游戏
     */
    smallgame("小游戏",5)
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
