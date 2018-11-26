package com.account.service;

import com.account.domain.AccountDetail;
import com.account.rpc.dto.AccountDetailDto;
import com.common.mongo.MongoService;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountDetailMgDbService extends MongoService<AccountDetail> {
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
    public List<AccountDetailDto> queryDetailList(Long proxyId, String pin, Integer page, Integer size);

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
    /**
     * 根据时间查询充值人数 dayStatus 1 全部 2 当天  3 本周 4 本月
     * @param dto
     * @return
     */
    public BigDecimal queryChargeUsers(AccountDetailDto dto);
    /**
     * 根据时间查询充值总额 dayStatus1 全部 2 当天  3 本周 4 本月
     * @param dto
     * @return
     */
    public BigDecimal queryChargeAmount(AccountDetailDto dto);
    /**
     * 根据时间查询新增充值人数 dayStatus 1 全部 2 当天  3 本周 4 本月
     * @param dto
     * @return
     */
    public BigDecimal queryChargeNewUsers(AccountDetailDto dto);

    /**
     * 根据时间查询代理商盈亏数据 dayStatus1 全部 2 当天  3 本周 4 本月
     * @param dto
     * @return
     */
    public BigDecimal queryProxyProfile(AccountDetailDto dto);
}
