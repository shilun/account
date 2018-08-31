package com.account.service.impl; 

import java.util.List;

import javax.annotation.Resource;

import com.common.util.AbstractBaseDao;
import com.common.util.DefaultBaseService;

import com.account.domain.Account;
import com.account.dao.AccountDao;
import com.account.service.AccountService;
import org.springframework.stereotype.Service;


/**
 * 
 * @desc 账户信息 account
 *
 */
@Service
public class AccountServiceImpl extends DefaultBaseService<Account> implements AccountService  {

	@Resource
	private AccountDao accountDao;
	
	
	@Override
	public AbstractBaseDao<Account> getBaseDao() {
		return accountDao;
	}
	
}
