package com.account.domain.module;

import com.common.util.IGlossary;

import java.util.HashMap;
import java.util.Map;

/**
 * 币种
 */
public enum TokenTypeEnum implements IGlossary {
    RMB("人民币",1),
    GoldCoin("金币",2);
    TokenTypeEnum(String name,Integer value) {
        this.name = name;
        this.value=value;
    }
    private static Map<String,TokenTypeEnum> maps;
    private static Map<Integer,TokenTypeEnum> valueMaps;
    static {
        maps=new HashMap<>();
        valueMaps=new HashMap<>();
        TokenTypeEnum[] values = TokenTypeEnum.values();
        for(TokenTypeEnum item:values){
            maps.put(item.name(),item);
            valueMaps.put(item.getValue(),item);
        }
    }

    public static TokenTypeEnum findByName(String name){
        return maps.get(name);
    }
    public static TokenTypeEnum findByValue(Integer value){
        return valueMaps.get(value);
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
