package com.account.service.impl;

import com.account.dao.AccountDetailtDao;
import com.account.domain.AccountDetail;
import com.account.service.AccountDetailtService;
import com.common.util.AbstractBaseDao;
import com.common.util.DefaultBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 
 * @desc 账本流水账 
 *
 */
@Service
public class AccountDetailtServiceImpl extends DefaultBaseService<AccountDetail> implements AccountDetailtService  {

	@Resource
	private AccountDetailtDao accountDetailtDao;


	@Override
	public AbstractBaseDao<AccountDetail> getBaseDao() {
		return accountDetailtDao;
	}

}
