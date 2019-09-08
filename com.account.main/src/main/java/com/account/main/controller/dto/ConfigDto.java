package com.account.main.controller.dto;

import com.common.util.AbstractDTO;

/**
 * 配置管理
 */
public class ConfigDto extends AbstractDTO {
    /**配置名称*/
    private String name;
    /**配置键*/
    private String keyName;
    /**值*/
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
