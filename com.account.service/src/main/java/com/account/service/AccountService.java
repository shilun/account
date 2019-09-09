package com.account.service;

import com.account.domain.Account;
import com.account.domain.AccountDetail;
import com.account.domain.module.DetailStatusEnum;
import com.account.domain.module.TokenTypeEnum;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.common.exception.ApplicationException;
import com.common.exception.BizException;
import com.common.mongo.MongoService;
import com.common.util.GlosseryEnumUtils;
import com.common.util.StringUtils;
import com.common.util.model.YesOrNoEnum;
import net.sf.json.JSONObject;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.List;

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
