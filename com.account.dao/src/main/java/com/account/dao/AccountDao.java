package com.account.dao;

import com.account.domain.Account;
import com.common.util.AbstractBaseDao;

import java.util.Map;

/**
 * 
 * @desc 账户信息 account
 *
 */
public interface AccountDao extends AbstractBaseDao<Account>{
	
	
	Double queryAmount(Account account);
	
}
