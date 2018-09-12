package com.account.rpc.dto;

import com.common.util.GlosseryEnumUtils;
import com.common.util.IGlossary;

import java.util.HashMap;
import java.util.Map;

/**
 * 币种
 */
public enum TokenTypeEnum implements IGlossary {
    RMB("人民币",1),
    GoldCoin("金币",2),
    RMB_TEST("测试账户",3),
    GoldCoin_Test("测试金币",4);

    TokenTypeEnum(String name,Integer value) {
        this.name = name;
        this.value=value;
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
    private Integer value;
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
