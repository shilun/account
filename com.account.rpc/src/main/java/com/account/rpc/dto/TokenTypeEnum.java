package com.account.domain.module;

import java.util.HashMap;
import java.util.Map;

/**
 * 币种
 */
public enum TokenTypeEnum {
    RMB("人民币"),
    GoldCoin("金币"),
    RMB_TEST("测试账户"),
    GoldCoin_Test("测试金币");

    TokenTypeEnum(String label) {
        this.lable = label;
    }
    private static Map<String,TokenTypeEnum> maps;
    static {
        maps=new HashMap<>();
        TokenTypeEnum[] values = TokenTypeEnum.values();
        for(TokenTypeEnum item:values){
            maps.put(item.name(),item);
        }
    }

    public static TokenTypeEnum findByName(String name){
        return maps.get(name);
    }

    private String lable;
    private String name;

    public String getLable() {
        return lable;
    }


    public String getName() {
        return name();
    }
}
