package com.account.service; 
import com.account.rpc.dto.AccountDetailDto;
import com.account.rpc.dto.TokenTypeEnum;
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

    /**
     * 账本详情
     * @param proxyId
     * @param pin
     * @param page
     * @param size
     * @return
     */
    public List<AccountDetailDto> queryDetailList(Long proxyId,String pin,Integer page,Integer size);

    /**
     * 账本详情
     * @param dto
     * @return
     */
    public List<AccountDetailDto> queryDetailList(AccountDetailDto dto);
}
