package com.account.service;

import com.account.domain.Config;
import com.common.mongo.MongoService;

import java.math.BigDecimal;

/**
 * @desc 配置管理 config
 */
public interface ConfigService extends MongoService<Config> {


    /**
     * 查询汇率
     * @param sourceType
     * @param targetType
     * @return
     */
    public BigDecimal findRate(Integer sourceType, Integer targetType);
}
