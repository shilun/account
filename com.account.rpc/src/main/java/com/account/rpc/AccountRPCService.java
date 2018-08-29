package com.account.rpc;

import com.account.rpc.dto.AccountDto;
import com.account.rpc.dto.InvertBizDto;
import com.common.util.RPCResult;

import java.util.List;

/**
 * 账户服务接口
 */
public interface AccountRPCService {


    /**
     * 查看用户币信息
     *
     * @param pin
     * @return
     */
    RPCResult<List<AccountDto>> findByPinAndTokenType(String pin);

    /**
     * 执行业务
     *
     * @param dto
     * @return
     */
    RPCResult<Boolean> invertBiz(InvertBizDto dto);

}
