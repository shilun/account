package com.account.dao;

/**
 * 
 * @desc 账户信息 account
 *
 */
public interface AccountDao <Account>{
	
	
	Double queryAmount(Account account);
	
}
