package com.account.service; 
import com.account.rpc.dto.AccountDetailDto;
import com.common.util.AbstractBaseService;
import com.account.domain.AccountDetail;

import java.math.BigDecimal;
import java.util.List;

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
    public void changeTo(Long proxyId, String pin, Integer sourceType, BigDecimal sourceAmount, Integer targetType);

    public List<AccountDetailDto> queryDetailList(Long proxyId,String pin,Integer page,Integer size);
}
