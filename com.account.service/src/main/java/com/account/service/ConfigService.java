package com.account.service;

import com.common.util.AbstractBaseService;
import com.account.domain.Config;

import java.math.BigDecimal;

/**
 * @desc 配置管理 config
 */
public interface ConfigService extends AbstractBaseService<Config> {


    /**
     * 查询汇率
     * @param sourceType
     * @param targetType
     * @return
     */
    public BigDecimal findRate(Integer sourceType, Integer targetType);
}
