package com.account.rpc;

import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.BizTypeEnum;
import com.account.rpc.dto.InvertBizDto;
import com.common.util.RPCResult;

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
    RPCResult<Boolean> invertBiz(InvertBizDto dto);

    /**
     * 批量执行业务
     * amount 正数时 账户加余额 负数时增加余额
     * freeze 正数 则从amount 转账到 freeze 则，负数时则直接减少锁定余额
     * @param dtos
     * @return
     */
    RPCResult<List<String>> invertBizs(List<InvertBizDto> dtos);

}
