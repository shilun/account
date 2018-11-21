package com.account.rpc.dto;

import com.common.util.IGlossary;

/**
 * 系统类型
 */
public enum BizTypeEnum implements IGlossary {
    /**
     * 订单系统
     */
    order("订单系统",1),
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
    smallgame("小游戏",5),
    /**
     * 签到系统
     */
    qiandao("签到系统",6),
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
