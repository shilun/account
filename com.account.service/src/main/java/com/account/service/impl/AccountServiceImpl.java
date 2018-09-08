package com.account.service.impl; 

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import com.account.domain.AccountDetail;
import com.account.domain.module.DetailStatusEnum;
import com.account.rpc.dto.InvertBizDto;
import com.account.service.AccountDetailtService;
import com.common.exception.BizException;
import com.common.util.AbstractBaseDao;
import com.common.util.DefaultBaseService;

import com.account.domain.Account;
import com.account.dao.AccountDao;
import com.account.service.AccountService;
import com.common.util.model.YesOrNoEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 
 * @desc 账户信息 account
 *
 */
@Service
public class AccountServiceImpl extends DefaultBaseService<Account> implements AccountService  {

	@Resource
	private AccountDao accountDao;

	@Resource
	private AccountDetailtService accountDetailtService;
	
	@Override
	public AbstractBaseDao<Account> getBaseDao() {
		return accountDao;
	}


	@Transactional
	public void newBiz(InvertBizDto dto) {
		Account query = new Account();
		query.setProxyId(dto.getProxyId());
		query.setPin(dto.getPin());
		query.setTokenType(dto.getTokenType().name());
		Account account = findByOne(query);
		if (account == null) {
			account = new Account();
			account.setTokenType(dto.getTokenType().name());
			account.setFreeze(BigDecimal.ZERO);
			account.setAmount(BigDecimal.ZERO);
			account.setProxyId(dto.getProxyId());
			account.setPin(dto.getPin());
		}
		AccountDetail detail = new AccountDetail();
		detail.setPin(dto.getPin());
		detail.setProxyId(dto.getProxyId());
		detail.setTokenType(dto.getTokenType().name());
		detail.setStatus(YesOrNoEnum.YES.getValue());
		detail.setBizType(dto.getBizType().getValue());
		detail.setBizId(dto.getBizId());
		detail.setBeforeAmount(account.getAmount());
		detail.setBeforeFreeze(account.getFreeze());
		if (dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
			account.setAmount(account.getAmount().add(dto.getAmount()));
		}
		if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			account.getAmount().subtract(dto.getAmount());
		}
		if (dto.getFreeze().compareTo(BigDecimal.ZERO) > 0) {
			account.setFreeze(account.getFreeze().add(dto.getFreeze()));
		}
		if (dto.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
			account.setAmount(account.getAmount().subtract(dto.getFreeze()));
			account.setFreeze(account.getFreeze().subtract(dto.getFreeze()));
		}
		if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new BizException("invertBiz.account.error", "执行业务失败,余额不足");
		}
		if (account.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
			throw new BizException("invertBiz.freeze.error", "执行业务失败,冻结账户不足");
		}
		detail.setChangeAmount(dto.getAmount());
		detail.setChangeFreeze(dto.getFreeze());

		detail.setAfterAmount(account.getAmount());
		detail.setAfterFreeze(account.getFreeze());

		if (account.getId() == null) {
			save(account);
		} else {
			Account upEntity = new Account();
			upEntity.setId(account.getId());
			upEntity.setAmount(account.getAmount());
			upEntity.setFreeze(account.getFreeze());
			up(upEntity);
		}
		detail.setStatus(DetailStatusEnum.Normal.getValue());
		accountDetailtService.add(detail);
	}
	
}
