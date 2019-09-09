package com.account.rpc;

import com.account.rpc.dto.*;
import com.common.util.RPCResult;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 账户服务接口
 */
public interface AccountRPCService {


    /**
     * 查询账本信息
     * @param proxyId
     * @param pin
     * @param tokenType   * 保险柜 1   现金 2
     * @param testStatus  * 是否为测试 1 否 2 是
     * @return
     */
    RPCResult<AccountDto>  findAccount(Long proxyId,String pin,Integer tokenType,Integer testStatus);
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
    RPCResult<BigDecimal> invertBiz(InvertBizDto dto);


    /**
     * 账户转账
     * @param proxyId 代理商id
     * @param pin 用户 pin
     * @param sourceType 源币种
     * @param sourceAmount 源数量
     * @param targetType 目标币种
     * @return
     */
    RPCResult changeTo(Long proxyId, String pin, Integer sourceType,BigDecimal sourceAmount, Integer targetType);


}
