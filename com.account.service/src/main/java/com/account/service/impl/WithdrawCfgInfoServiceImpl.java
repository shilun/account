package com.account.service.impl;

import com.account.domain.WithdrawCfgInfo;
import com.account.service.WithdrawCfgInfoService;
import com.common.mongo.AbstractMongoService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author: CSL
 * @Date: 2018/9/11 11:40
 */
@Service
public class WithdrawCfgInfoServiceImpl extends AbstractMongoService<WithdrawCfgInfo> implements WithdrawCfgInfoService {

    private static Logger logger = LoggerFactory.getLogger(WithdrawCfgInfoServiceImpl.class);
    @Override
    protected Class getEntityClass() {
        return WithdrawCfgInfo.class;
    }


}
