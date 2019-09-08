package com.account.service;

import com.account.domain.Account;
import com.account.rpc.dto.InvertBizDto;
import com.common.mongo.MongoService;

import java.util.List;

/**
 * 
 * @desc 账户信息 account
 *
 */
public interface AccountService extends MongoService<Account> {

}
