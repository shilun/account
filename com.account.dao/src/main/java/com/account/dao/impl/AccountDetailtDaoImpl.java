package com.account.dao.impl; 
import java.util.List;

import com.common.util.DefaultBaseDao;

import com.account.dao.AccountDetailtDao;
import com.account.domain.AccountDetailt;
import org.springframework.stereotype.Component;

/**
 * 
 * @desc 账本流水账 
 *
 */
@Component
public class AccountDetailtDaoImpl extends DefaultBaseDao<AccountDetailt> implements AccountDetailtDao  {
	private static final long serialVersionUID = 1L;
	private final static String NAMESPACE = "com.account.dao.AccountDetailtDao.";
	
	@Override
	public String getNameSpace(String statement) {		
		return NAMESPACE+statement;
	}
	
}
