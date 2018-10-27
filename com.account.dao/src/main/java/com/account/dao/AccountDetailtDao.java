package com.account.dao; 
import com.account.domain.AccountDetail;
import com.common.util.AbstractBaseDao;
/**
 * 
 * @desc 账本流水账 
 *
 */
public interface AccountDetailtDao extends AbstractBaseDao<AccountDetail>{
	
	Double querySum(AccountDetail detail);
	
	
}
