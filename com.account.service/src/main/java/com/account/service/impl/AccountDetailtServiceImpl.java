package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.BizTypeEnum;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.BeanCoper;
import com.common.util.GlosseryEnumUtils;
import com.common.util.StringUtils;
import com.common.util.model.OrderTypeEnum;
import com.common.util.model.YesOrNoEnum;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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


    @Override
    protected Class getEntityClass() {
        return AccountDetail.class;
    }

    @Transactional
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType) {
        if (proxyId == null) {
            throw new BizException("proxyId 属性 不能为空");
        }
        if (sourceAmount == null || sourceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("转账金额必须大于0");
        }
        Account accountQuery = new Account();
        accountQuery.setPin(pin);
        accountQuery.setProxyId(proxyId);
        List<Account> list = accountService.query(accountQuery, true);
        if (list == null) {
            list = new ArrayList<>();
        }
        Account sourceAccount = null;
        Account targetAccount = null;
        for (TokenTypeEnum type : TokenTypeEnum.values()) {
            boolean moneyed = false;
            for (Account item : list) {
                if (type.getValue().intValue() == item.getTokenType()) {
                    moneyed = true;
                    sourceAccount = item;
                    continue;
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

        //源账本变化记录
        AccountDetail sourceDetail = new AccountDetail();

        sourceDetail.setBizType(BizTypeEnum.account.getValue());
        String uuid = StringUtils.getUUID();
        sourceDetail.setBizId(uuid + "_" + GlosseryEnumUtils.getItem(TokenTypeEnum.class, sourceType).name());
        sourceDetail.setTokenType(sourceType);
        sourceDetail.setPin(pin);
        sourceDetail.setProxyId(proxyId);
        sourceDetail.setChangeAmount(BigDecimal.ZERO.subtract(sourceAmount));
        sourceDetail.setBeforeAmount(sourceAccount.getAmount());
        sourceDetail.setAfterAmount(sourceAccount.getAmount().subtract(sourceAmount));


        //目标账本变化记录
        AccountDetail targetDetail = new AccountDetail();
        targetDetail.setBizType(BizTypeEnum.account.getValue());
        targetDetail.setBizId(uuid + "_" + GlosseryEnumUtils.getItem(TokenTypeEnum.class, targetType).name());
        targetDetail.setTokenType(targetType);
        targetDetail.setPin(pin);
        targetDetail.setProxyId(proxyId);
        targetDetail.setChangeAmount(sourceAmount);
        targetDetail.setBeforeAmount(targetAccount.getAmount());
        targetDetail.setAfterAmount(targetAccount.getAmount().add(sourceAmount));

        sourceDetail.setRemark(targetDetail.getBizId());
        targetDetail.setRemark(sourceDetail.getBizId());
        //源账本减去记录
        sourceAccount.setAmount(sourceAccount.getAmount().subtract(sourceAmount));
        if (sourceAccount.getAmount().compareTo(targetAccount.getAmount()) < 0) {
            throw new BizException("changeTo.error", "转账失败,余额不足");
        }
        //同步至mongodb
        if (sourceAccount.getId() == null) {
            sourceAccount.setVer(0);
            accountService.insert(sourceAccount);
        } else {
            Query upQuery = new Query();
            Criteria criteria = Criteria.where("pin").is(pin);
            criteria.and("proxyId").is(proxyId);
            criteria.and("tokenType").is(sourceType);
            criteria.and("ver").is(sourceAccount.getVer());
            upQuery.addCriteria(criteria);
            Update upAccount = new Update();
            upAccount.inc("ver", 1);
            upAccount.inc("amount", sourceAmount.negate());
            UpdateResult updateResult = primaryTemplate.updateFirst(upQuery, upAccount, Account.class);
            if (1 != updateResult.getMatchedCount()) {
                throw new ApplicationException("mongodb异常");
            }
        }
        if (targetAccount.getId() == null) {
            sourceAccount.setVer(0);
            accountService.insert(targetAccount);
        } else {
            Query upQuery = new Query();
            Criteria criteria = Criteria.where("pin").is(pin);
            criteria.and("proxyId").is(proxyId);
            criteria.and("tokenType").is(sourceType);
            criteria.and("ver").is(sourceAccount.getVer());
            upQuery.addCriteria(criteria);
            Update upAccount = new Update();
            upAccount.inc("ver", 1);
            upAccount.inc("amount", targetAccount.getAmount());
            UpdateResult updateResult = primaryTemplate.updateFirst(upQuery, upAccount, Account.class);
            if (1 != updateResult.getMatchedCount()) {
                throw new ApplicationException("mongodb乐观锁异常");
            }
        }
        insert(sourceDetail);
        insert(targetDetail);
    }

    @Override
    public List<AccountDetailDto> queryDetailList(Long proxyId, String pin, Integer page, Integer size) {
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        AccountDetail query = new AccountDetail();
        query.setProxyId(proxyId);
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
