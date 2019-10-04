package com.account.service;

import com.account.domain.Account;
import com.account.rpc.dto.InvertBizDto;
import com.common.mongo.MongoService;

import java.math.BigDecimal;

/**
 * @desc 账户信息 account
 */
public interface AccountService extends MongoService<Account> {
    /**
     * 执行业务
     *
     * @param dto
     */
    public BigDecimal newBiz(InvertBizDto dto);
}
