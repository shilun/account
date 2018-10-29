package com.account.dao.impl;

import com.common.util.DefaultBaseDao;

import com.account.dao.AccountDetailtDao;
import com.account.domain.AccountDetail;
import org.springframework.stereotype.Component;

import java.util.Map;

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

	@Override
	public Double querySum(AccountDetail detail) {
		Double querySum =  this.select(this.getNameSpace("querySum"), detail);
		return querySum;
	}
}
