package com.account.dao.impl; 
import java.util.List;

import com.common.util.DefaultBaseDao;

import com.account.dao.MoneyPassCfgDao;
import com.account.domain.MoneyPassCfg;
import org.springframework.stereotype.Component;

/**
 * 
 * @desc 提款密码 money_pass_cfg
 *
 */
@Component
public class MoneyPassCfgDaoImpl extends DefaultBaseDao<MoneyPassCfg> implements MoneyPassCfgDao  {
	private static final long serialVersionUID = 1L;
	private final static String NAMESPACE = "com.account.dao.MoneyPassCfgDao.";
	
	@Override
	public String getNameSpace(String statement) {		
		return NAMESPACE+statement;
	}
	
}
