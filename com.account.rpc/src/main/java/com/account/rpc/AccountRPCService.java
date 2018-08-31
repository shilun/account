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
     *
     * @param dto
     * @return
     */
    RPCResult<Boolean> invertBiz(InvertBizDto dto);

    /**
     * 回滚业务
     * @param bizType
     * @param bizId
     * @return
     */
    RPCResult<Boolean> invertBizBack(BizTypeEnum bizType, Long bizId);

}
