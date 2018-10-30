package com.account.service;

import com.account.domain.UserBank;
import com.common.mongo.MongoService;

public interface UserBankService extends MongoService<UserBank> {

    /**
     * 修改用户银行卡号
     * @param entity
     */
    void upUserBank(UserBank entity);

    /**
     * 删除用户银行卡
     * @param bankId
     */
    void delUserBank(Long bankId, String pin);
}
