package com.account.service; 
import com.account.rpc.dto.AccountDetailDto;
import com.common.util.AbstractBaseService;
import com.account.domain.AccountDetail;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    public Page<AccountDetailDto> queryDetailList(AccountDetailDto dto);

    /**
     * 平台平均充值量
     * @return
     */
    public Map<String,Object> avargCharge(AccountDetailDto dto);

    public BigDecimal queryChargeUsers(AccountDetailDto dto);

    public BigDecimal queryChargeAmount(AccountDetailDto dto);
}
