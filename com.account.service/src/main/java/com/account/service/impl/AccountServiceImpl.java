package com.account.service.impl;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.WithdrawCfgInfo;
import com.account.rpc.dto.BizTokenEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.account.service.AccountService;
import com.account.service.WithdrawCfgInfoService;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.AbstractMongoService;
import com.common.util.*;
import com.common.util.model.YesOrNoEnum;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;


/**
 * @desc 账户信息 account
 */
@Service
public class AccountServiceImpl extends AbstractMongoService<Account> implements AccountService {
    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Resource
    private AccountDetailtService accountDetailtService;

    @Reference
    private UserRPCService userRPCService;
    @Resource
    private WithdrawCfgInfoService withdrawCfgInfoService;

    @Override
    protected Class getEntityClass() {
        return Account.class;
    }

}
