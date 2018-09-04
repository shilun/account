package com.account.domain;   

import java.util.Date;

import com.common.util.AbstractBaseEntity;
/**
 * 
 * @desc 提款密码 money_pass_cfg
 *
 */
public class MoneyPassCfg extends AbstractBaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	/**编号*/
	private Long id;
	/**pin*/
	private String pin;
	/**代理商*/
	private Long proxyId;
	/**提款密码*/
	private String pass;
	/**删除状态*/
	private Integer delStatus;
	/**创建时间*/
	private Date createTime;
	/**修改时间*/
	private Date updateTime;
	public Long getId(){
		return this.id;
	}
	public void setId(Long id){
	 	this.id=id;
	}
	public String getPin(){
		return this.pin;
	}
	public void setPin(String pin){
	 	this.pin=pin;
	}
	public Long getProxyId(){
		return this.proxyId;
	}
	public void setProxyId(Long proxyId){
	 	this.proxyId=proxyId;
	}
	public String getPass(){
		return this.pass;
	}
	public void setPass(String pass){
	 	this.pass=pass;
	}
	public Integer getDelStatus(){
		return this.delStatus;
	}
	public void setDelStatus(Integer delStatus){
	 	this.delStatus=delStatus;
	}
	public Date getCreateTime(){
		return this.createTime;
	}
	public void setCreateTime(Date createTime){
	 	this.createTime=createTime;
	}
	public Date getUpdateTime(){
		return this.updateTime;
	}
	public void setUpdateTime(Date updateTime){
	 	this.updateTime=updateTime;
	}
}
