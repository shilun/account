package com.account.dao.impl; 
import java.util.List;
import java.util.Map;

import com.account.dao.AccountDao;
import com.common.util.DefaultBaseDao;

import com.account.domain.Account;
import org.springframework.stereotype.Component;

/**
 * 
 * @desc 账户信息 account
 *
 */
@Component
public class AccountDaoImpl extends DefaultBaseDao<Account> implements AccountDao {
	private static final long serialVersionUID = 1L;
	private final static String NAMESPACE = "com.account.dao.AccountDao.";
	
	@Override
	public String getNameSpace(String statement) {		
		return NAMESPACE+statement;
	}

	@Override
	public Double queryAmount(Account account) {
		return this.select(this.getNameSpace("queryAmount"), account);
	}
}
