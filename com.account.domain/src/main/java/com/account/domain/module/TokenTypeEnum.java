package com.account.domain.module;

/**
 * 币种
 */
public enum TokenTypeEnum {
    RMB("人民币"),
    GoldCoin("金币");

    TokenTypeEnum(String label) {
        this.lable = label;
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
