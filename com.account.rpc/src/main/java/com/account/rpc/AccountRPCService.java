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
     * 查询账户
     * @param pin
     * @return
     */
    RPCResult<AccountDto>  findAccount(String pin);

    /**
     * 执行业务
     * amount 正数时 账户加余额 负数时增加余额
     * freeze 正数 则从amount 转账到 freeze 则，负数时则直接减少锁定余额
     * @param dto
     * @return
     */
    RPCResult<BigDecimal> invertBiz(InvertBizDto dto);



}
