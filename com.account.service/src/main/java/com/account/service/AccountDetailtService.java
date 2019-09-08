package com.account.service;

import com.account.domain.AccountDetail;
import com.account.rpc.dto.AccountDetailDto;
import com.common.mongo.MongoService;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * @desc 账本流水账 
 *
 */
public interface AccountDetailtService extends MongoService<AccountDetail> {
    /**
     * 转账
     * @param proxyId
     * @param pin
     * @param sourceType
     * @param sourceAmount
     * @param targetType
     */
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType);

    /**
     * 账本详情
     * @param proxyId
     * @param pin
     * @param page
     * @param size
     * @return
     */
    public List<AccountDetailDto> queryDetailList(Long proxyId,String pin,Integer page,Integer size);

}
