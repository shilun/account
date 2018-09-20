package com.account.service; 
import com.account.rpc.dto.InvertBizDto;
import com.common.util.AbstractBaseService;
import com.account.domain.Account;

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


}
