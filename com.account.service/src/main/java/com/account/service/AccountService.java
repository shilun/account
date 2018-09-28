package com.account.service; 
import com.account.rpc.dto.InvertBizDto;
import com.common.util.AbstractBaseService;
import com.account.domain.Account;

import java.util.List;

/**
 * 
 * @desc 账户信息 account
 *
 */
public interface AccountService extends AbstractBaseService<Account> {
    /**
     * 转账
     * @param dto
     */

    public void newBiz(InvertBizDto dto);


    /**
     * 冻结所有币
     * @param proxyId
     * @param pin
     * @param tokenType
     */
    List<Account> freezeAll(Long proxyId, String pin, Integer tokenType, Integer testStatus);
}
