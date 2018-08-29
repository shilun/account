package com.account.service.impl; 

import java.util.List;

import javax.annotation.Resource;

import com.common.util.AbstractBaseDao;
import com.common.util.DefaultBaseService;

import com.account.domain.AccountDetailt;
import com.account.dao.AccountDetailtDao;
import com.account.service.AccountDetailtService;
import com.common.util.RPCResult;
import com.passport.rpc.AdminRPCService;
import com.passport.rpc.dto.UserDTO;


/**
 * 
 * @desc 账本流水账 
 *
 */
public class AccountDetailtServiceImpl extends DefaultBaseService<AccountDetailt> implements AccountDetailtService  {

	@Resource
	private AccountDetailtDao accountDetailtDao;

	@Resource
	private AdminRPCService adminRPCService;
	
	@Override
	public AbstractBaseDao<AccountDetailt> getBaseDao() {
		return accountDetailtDao;
	}


	public void tt(){
		RPCResult<UserDTO> fdsafdsa = adminRPCService.login("", "fdsafdsa");
		if(fdsafdsa.getSuccess()){
			UserDTO data = fdsafdsa.getData();

		}
		else{

		}
	}
}
