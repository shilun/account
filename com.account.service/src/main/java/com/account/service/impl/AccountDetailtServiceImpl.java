package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.dto.AccountDetailDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.ConfigService;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.BeanCoper;
import com.common.util.model.YesOrNoEnum;
import com.passport.rpc.UserRPCService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @desc 账本流水账
 */
@Service
public class AccountDetailtServiceImpl extends AbstractMongoService<AccountDetail> implements AccountDetailtService {


    @Resource
    private AccountService accountService;

    @Resource
    private ConfigService configService;

    @Reference
    private UserRPCService userRPCService;

    @Override
    protected Class getEntityClass() {
        return AccountDetail.class;
    }

    @Transactional
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType) {
        Account query = new Account();
        query.setPin(pin);
        query.setProxyId(proxyId);
        List<Account> list = accountService.query(query);
        for (TokenTypeEnum type : TokenTypeEnum.values()) {
            boolean moneyed = false;
            for (Account item : list) {
                if (type.getValue().intValue() == item.getTokenType()) {
                    moneyed = true;
                    break;
                }
            }
            if (!moneyed) {
                Account e = new Account();
                e.setPin(pin);
                e.setTokenType(type.getValue());
                e.setProxyId(proxyId);
                e.setAmount(BigDecimal.ZERO);
                e.setTokenType(type.getValue());
                e.setStatus(YesOrNoEnum.NO.getValue());
                list.add(e);
            }
        }
        Account sourceAccount = null;
        Account targetAccount = null;
        for (Account item : list) {
            if (sourceType.intValue()==item.getTokenType()) {
                sourceAccount = item;
            }
            if (targetType.intValue()==item.getTokenType()) {
                targetAccount = item;
            }
            if (sourceAccount != null && targetAccount != null) {
                break;
            }
        }

//        BigDecimal rate = configService.findRate(sourceType, targetType);
//        BigDecimal total = sourceAmount.multiply(rate);

        sourceAccount.setAmount(sourceAccount.getAmount().subtract(sourceAmount));

        if (sourceAccount.getAmount().compareTo(sourceAccount.getAmount()) < 0) {
            throw new BizException("changeTo.error", "转账失败,余额不足");
        }
        Account upSourceAccount = null;
        if (sourceAccount.getId() == null) {
            upSourceAccount = sourceAccount;
        } else {
            upSourceAccount = new Account();
            upSourceAccount.setId(sourceAccount.getId());
        }
        upSourceAccount.setAmount(sourceAccount.getAmount());
        accountService.save(upSourceAccount);
        Account upTargetAccount = null;
        if (targetAccount.getId() == null) {
            upTargetAccount = targetAccount;
        } else {
            upTargetAccount = new Account();
            upTargetAccount.setId(targetAccount.getId());
        }
        upTargetAccount.setAmount(targetAccount.getAmount().add(sourceAmount));
        accountService.save(upTargetAccount);
    }

    @Override
    public List<AccountDetailDto> queryDetailList(Long proxyId, String pin, Integer page, Integer size) {
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        AccountDetail query = new AccountDetail();
        query.setProxyId(proxyId);
        query.setPin(pin);
        query.setOrderColumn("id");
        query.setOrderTpe(2);
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
