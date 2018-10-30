package com.account.service;

import com.account.domain.UserDrawalLog;
import com.common.mongo.MongoService;

import java.math.BigDecimal;

public interface UserDrawalLogService extends MongoService<UserDrawalLog> {
    /**
     * 用户提钱
     *
     * @param proxyId
     * @param pin
     * @param amount
     */
    void drawal(Long proxyId, String pin, BigDecimal amount, String msg);

    /**
     * 生成提钱验证码
     * @param proxyId
     * @param pin
     * @param phoneNo
     */
    void drawalBuildMsg(Long proxyId, String pin, String phoneNo);
}
