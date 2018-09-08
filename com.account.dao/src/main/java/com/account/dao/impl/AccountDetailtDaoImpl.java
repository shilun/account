package com.account.dao.impl;

import com.common.util.DefaultBaseDao;

import com.account.dao.AccountDetailtDao;
import com.account.domain.AccountDetail;
import org.springframework.stereotype.Component;

/**
 * 
 * @desc 账本流水账 
 *
 */
@Component
public class AccountDetailtDaoImpl extends DefaultBaseDao<AccountDetail> implements AccountDetailtDao  {
	private static final long serialVersionUID = 1L;
	private final static String NAMESPACE = "com.account.dao.AccountDetailDao.";
	
	@Override
	public String getNameSpace(String statement) {		
		return NAMESPACE+statement;
	}
	
}
