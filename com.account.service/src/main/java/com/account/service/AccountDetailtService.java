package com.account.service; 
import com.account.rpc.dto.TokenTypeEnum;
import com.common.util.AbstractBaseService;
import com.account.domain.AccountDetail;

import java.math.BigDecimal;

/**
 * 
 * @desc 账本流水账 
 *
 */
public interface AccountDetailtService extends AbstractBaseService<AccountDetail> {
    /**
     * 转账
     * @param proxyId
     * @param pin
     * @param sourceType
     * @param sourceAmount
     * @param targetType
     */
    public void changeTo(Long proxyId, String pin, TokenTypeEnum sourceType, BigDecimal sourceAmount, TokenTypeEnum targetType);
}
