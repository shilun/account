package com.account.domain;

import com.common.util.AbstractBaseEntity;

import java.math.BigDecimal;

/**
 * 
 * @desc 账户信息 account
 *
 */
public class Account extends AbstractBaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 代理商id
	 */
	private Long proxyId;
	/**pin*/
	private String pin;
	/**币种*/
	private String tokenType;
	/**总余额*/
	private BigDecimal amount;
	/**冻结*/
	private BigDecimal freeze;
	/**状态 1 启用 2 禁用*/
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getProxyId() {
		return proxyId;
	}

	public void setProxyId(Long proxyId) {
		this.proxyId = proxyId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFreeze() {
		return freeze;
	}

	public void setFreeze(BigDecimal freeze) {
		this.freeze = freeze;
	}
}
