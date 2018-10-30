package com.account.service.impl;

import com.account.domain.UserBank;
import com.account.service.UserBankService;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserBankServiceImpl extends AbstractMongoService<UserBank> implements UserBankService {


    @Override
    protected Class getEntityClass() {
        return UserBank.class;
    }


    @Override
    public void upUserBank(UserBank entity) {
        UserBank query = new UserBank();
        query.setPin(entity.getPin());
        query.setProxyId(entity.getProxyId());
        UserBank old = findByOne(query);
        old.setName(entity.getName());
        old.setBankName(entity.getBankName());
        old.setBankType(entity.getBankType());
        old.setCode(entity.getCode());
        old.setAddress(entity.getAddress());
        save(old);
    }

    @Override
    public void delUserBank(Long bankId, String pin) {
        UserBank old = findById(bankId);
        if (old == null || !StringUtils.equals(pin, old.getPin())) {
            throw new BizException("upUserBank.error", "修改账户银行卡失败");
        }
        delById(bankId);
    }
}
