package com.account.service.impl;

import com.account.domain.AccountDetail;
import com.account.rpc.dto.AccountDetailDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.common.mongo.AbstractMongoService;
import com.common.util.BeanCoper;
import com.common.util.model.OrderTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @desc 账本流水账
 */
@Service
public class AccountDetailtServiceImpl extends AbstractMongoService<AccountDetail> implements AccountDetailtService {


    @Resource
    private AccountService accountService;


    @Override
    protected Class getEntityClass() {
        return AccountDetail.class;
    }

    @Override
    public List<AccountDetailDto> queryDetailList(String pin, Integer page, Integer size) {
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        AccountDetail query = new AccountDetail();
        query.setPin(pin);
        query.setOrderColumn("id");
        query.setOrderType(OrderTypeEnum.DESC);
        List<AccountDetail> accountDetails = query(query);
        if (!accountDetails.isEmpty()) {
            for (AccountDetail detail : accountDetails) {
                AccountDetailDto accountDetailDto = new AccountDetailDto();
                BeanCoper.copyProperties(accountDetailDto, detail);
                accountDetailDtos.add(accountDetailDto);
            }
        }
        return accountDetailDtos;
    }


}
