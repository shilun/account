package com.account.rpc;

import com.account.rpc.dto.*;
import com.common.util.RPCResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务接口
 */
public interface AccountRPCService {

    /**
     * 查看用户账户
     * @param pin 用户id
     * @param proxyId 代理商
     * @return
     */
    RPCResult<List<AccountDto>> queryAccount(String pin, Long proxyId);

    /**
     * 执行业务
     * amount 正数时 账户加余额 负数时增加余额
     * freeze 正数 则从amount 转账到 freeze 则，负数时则直接减少锁定余额
     * @param dto
     * @return
     */
    RPCResult invertBiz(InvertBizDto dto);

    /**
     * 批量执行业务
     * amount 正数时 账户加余额 负数时增加余额
     * freeze 正数 则从amount 转账到 freeze 则，负数时则直接减少锁定余额
     * @param dtos
     * @return
     */
    RPCResult<List<String>> invertBizs(List<InvertBizDto> dtos);

    /**
     * 账户汇率转账
     * @param proxyId 代理商id
     * @param pin 用户 pin
     * @param sourceType 源币种
     * @param sourceAmount 源数量
     * @param targetType 目标币种
     * @return
     */
    RPCResult changeTo(Long proxyId, String pin, Integer sourceType,BigDecimal sourceAmount, Integer targetType);

    /**
     * 汇率查询
     * @param sourceType 源
     * @param targetType 目标
     * @return
     */
    RPCResult<BigDecimal> queryRate(Integer sourceType, Integer targetType);


    /**
     * 查询账本详情
     * @param proxyId
     * @param pin
     * @param page
     * @param size
     * @return
     */
    RPCResult<List<AccountDetailDto>> queryDetail(Long proxyId,String pin,Integer page,Integer size);
}
