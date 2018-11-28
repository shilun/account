package com.account.dao;

/**
 * 
 * @desc 账本流水账 
 *
 */
public interface AccountDetailtDao<AccountDetail>{
	
	Double querySum(AccountDetail detail);
	
	Integer queryNewCount(AccountDetail detail);
}
