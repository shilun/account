package com.account.service.impl;

import com.account.domain.Config;
import com.account.domain.module.TokenTypeEnum;
import com.account.service.ConfigService;
import com.common.mongo.AbstractMongoService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @desc 配置管理 config
 *
 */
@Service
public class ConfigServiceImpl extends AbstractMongoService<Config> implements ConfigService  {


	@Override
	protected Class getEntityClass() {
		return Config.class;
	}

	private Cache<String, BigDecimal> cache = CacheBuilder.newBuilder().initialCapacity(10)
			//设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
			.concurrencyLevel(5)
			//设置cache中的数据在写入之后的存活时间为10秒
			.expireAfterWrite(1, TimeUnit.MINUTES)
			//构建cache实例
			.build();


	public BigDecimal findRate(Integer sourceType, Integer targetType) {
		Config query = new Config();
		query.setKeyName("rate_" + TokenTypeEnum.findByValue(sourceType).name());
		query = findByOne(query);
		String content = query.getContent();
		JSONObject jsonObject = JSONObject.fromObject(content);
		return BigDecimal.valueOf(jsonObject.getDouble(TokenTypeEnum.findByValue(targetType).name()));
	}
}
