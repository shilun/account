package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.GlosseryEnumUtils;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;


/**
 * @desc 账户信息 account
 */
@Service
public class AccountServiceImpl extends AbstractMongoService<Account> implements AccountService {
    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Resource
    private AccountDetailtService accountDetailtService;

    @Override
    protected Class getEntityClass() {
        return Account.class;
    }

    @Transactional
    public BigDecimal newBiz(InvertBizDto dto) {
        if (StringUtils.isBlank(dto.getBizId())) {
            throw new BizException("dto.error.BizId", "数据验证失败, bizId null");
        }
        if (dto.getBizType() == null) {
            throw new BizException("dto.error.bizType", "数据验证失败 bizType null");
        }
        if (StringUtils.isBlank(dto.getPin())) {
            throw new BizException("dto.error.pin", "数据验证失败 pin null");
        }
        if (dto.getAmount() == null) {
            dto.setAmount(BigDecimal.ZERO);
        }
        BizTypeEnum bizType = GlosseryEnumUtils.getItem(BizTypeEnum.class, dto.getBizType().getValue());
        if (bizType == null) {
            throw new BizException("dto.error.bizType", "bizType 非法");
        }
        Account query = new Account();
        query.setPin(dto.getPin());
        Account account = findByOne(query, true);
        //账户充值总额
        if (account == null) {
            account = new Account();
            account.setAmount(BigDecimal.ZERO);
            account.setPin(dto.getPin());
            account.setVer(0);
            account.setStatus(YesOrNoEnum.YES.getValue());
        }
        AccountDetail detail = new AccountDetail();
        detail.setPin(dto.getPin());
        detail.setStatus(YesOrNoEnum.YES.getValue());
        detail.setBizId(dto.getBizId());
        detail.setRemark(dto.getRemark());
        detail.setOperator(dto.getOperator());
        detail.setBeforeAmount(account.getAmount());
        account.setAmount(account.getAmount().add(dto.getAmount()));
        detail.setChangeAmount(dto.getAmount());
        detail.setAfterAmount(account.getAmount());
        if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("pin:" + dto.getPin() + "消费：" + dto.getAmount() + "分，账户余额不足");
            throw new BizException("account.error", "账户余额不足");
        }
        detail.setStatus(YesOrNoEnum.YES.getValue());
        accountDetailtService.insert(detail);
        //修改充值总额
        if (StringUtils.isBlank(account.getId())) {
            insert(account);
        } else {
            Query upQuery = new Query();
            Criteria criteria = Criteria.where("pin").is(dto.getPin());
            criteria.and("ver").is(account.getVer());
            upQuery.addCriteria(criteria);
            Update upAccount = new Update();
            upAccount.inc("ver", 1);
            upAccount.set("amount", account.getAmount());
            UpdateResult updateResult = primaryTemplate.updateFirst(upQuery, upAccount, Account.class);
            if (1 != updateResult.getMatchedCount()) {
                throw new ApplicationException("mongodb乐观锁异常");
            }
        }
        return account.getAmount();
    }
}