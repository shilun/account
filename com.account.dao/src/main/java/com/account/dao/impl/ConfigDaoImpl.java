package com.account.dao.impl; 
import java.util.List;

import com.common.util.DefaultBaseDao;

import com.account.dao.ConfigDao;
import com.account.domain.Config;
import org.springframework.stereotype.Component;

/**
 * 
 * @desc 配置管理 config
 *
 */
@Component
public class ConfigDaoImpl extends DefaultBaseDao<Config> implements ConfigDao  {
	private static final long serialVersionUID = 1L;
	private final static String NAMESPACE = "com.account.dao.ConfigDao.";
	
	@Override
	public String getNameSpace(String statement) {		
		return NAMESPACE+statement;
	}
	
}
