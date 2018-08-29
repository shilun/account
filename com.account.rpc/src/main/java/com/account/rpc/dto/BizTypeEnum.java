package com.account.rpc.dto;

import com.common.util.IGlossary;

/**
 * 业务类型
 */
public enum BizTypeEnum implements IGlossary {
    /**
     * 棋牌
     */
    qipai("棋牌",1),
    /**
     * 彩票
     */
    caipiao("彩票",2),
    /**
     * 小游戏
     */
    smallgame("小游戏",3)
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
