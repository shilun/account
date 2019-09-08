package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.WithdrawCfgInfo;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailMgDbService;
import com.account.service.AccountMgDbService;
import com.account.service.AccountService;
import com.account.service.WithdrawCfgInfoService;
import com.alibaba.fastjson.JSON;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.GlosseryEnumUtils;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.session.ClientSession;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * mongoDB account账户信息
 */
@Service
public class AccountMgDbServiceImpl extends AbstractMongoService<Account> implements AccountMgDbService {
    private Logger logger = LoggerFactory.getLogger(AccountMgDbServiceImpl.class);
    @Resource
    private MongoClient mongoClient;
    /**
     * 执行account业务key
     */
    private String user_login_key = "account.newBiz.pin.{0}";
    /**
     * 锁定用户所有币key
     */
    private String accountFreezeAll_key = "account.freezeAll.pin.{0}";

    @Resource
    private Executor executor;

    @Resource
    private AccountDetailMgDbService accountDetailMgDbService;

    @Override
    @Transactional
    public BigDecimal newBiz(InvertBizDto dto) {

        if (dto.getProxyId() == null) {
            throw new BizException("dto.error.proxyId", "数据验证失败,proxyId null");
        }
        if (dto.getTokenType() == null) {
            throw new BizException("dto.error.tokenType", "数据验证失败,tokenType null");
        }
        if (StringUtils.isBlank(dto.getBizId())) {
            throw new BizException("dto.error.BizId", "数据验证失败, bizId null");
        }
        if (dto.getBizType() == null) {
            throw new BizException("dto.error.bizType", "数据验证失败 bizType null");
        }
        if (StringUtils.isBlank(dto.getPin())) {
            throw new BizException("dto.error.pin", "数据验证失败 pin null");
        }
        if (dto.getTokenType() == null) {
            throw new BizException("dto.error.tokenType", "数据验证失败");
        }
        if (dto.getAmount() == null) {
            dto.setAmount(BigDecimal.ZERO);
        }
        AccountDetail findDetail = new AccountDetail();

        findDetail.setBizType(dto.getBizType());
        findDetail.setProxyId(dto.getProxyId());
        findDetail.setBizId(dto.getBizId());
        findDetail = accountDetailMgDbService.findByOne(findDetail);
        if (findDetail != null) {
            return null;
        }


        try {
            Account query = new Account();
            query.setProxyId(dto.getProxyId());
            query.setPin(dto.getPin());
            BizTypeEnum bizTypeEnum = GlosseryEnumUtils.getItem(BizTypeEnum.class, dto.getBizType());
            query.setTokenType(dto.getTokenType());
            Account account = findByOne(query);
            if (account == null) {
                account = new Account();
                account.setTokenType(dto.getTokenType());
                account.setAmount(BigDecimal.ZERO);
                account.setProxyId(dto.getProxyId());
                account.setPin(dto.getPin());
            }
            AccountDetail detail = new AccountDetail();
            detail.setPin(dto.getPin());
            detail.setProxyId(dto.getProxyId());
            detail.setTokenType(dto.getTokenType());
            detail.setStatus(YesOrNoEnum.YES.getValue());
            detail.setBizType(bizTypeEnum.getValue());
            detail.setBizId(dto.getBizId());
            detail.setBeforeAmount(account.getAmount());
            account.setAmount(account.getAmount().add(dto.getAmount()));
            detail.setChangeAmount(dto.getAmount());
            detail.setAfterAmount(account.getAmount());
            if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BizException("account.error", "账户余额不足");
            }
            if (account.getAmount().compareTo(account.getAmount()) < 0) {
                    throw new BizException("account.error", "账户金额不足");
            }
            if (account.getId() == null) {
                save(account);
            } else {
                Account upEntity = new Account();
                upEntity.setId(account.getId());
                upEntity.setAmount(account.getAmount());
                up(upEntity);
            }
            detail.setStatus(DetailStatusEnum.Normal.getValue());
            accountDetailMgDbService.save(detail);
            return account.getAmount();
        } catch (BizException biz) {
            throw biz;
        } catch (Exception e) {
            logger.error("newBiz.error", e);
            throw new ApplicationException("newBiz.error");
        }

    }

    @Override
    protected Class getEntityClass() {
        return Account.class;
    }
}
