package com.account.domain;   

import java.util.Date;

import com.common.util.AbstractBaseEntity;
/**
 * 
 * @desc 配置管理 config
 *
 */
public class Config extends AbstractBaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	/**编号*/
	private Long id;
	/**配置名称*/
	private String name;
	/**配置键*/
	private String keyName;
	/**值*/
	private String content;
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
	public String getName(){
		return this.name;
	}
	public void setName(String name){
	 	this.name=name;
	}
	public String getKeyName(){
		return this.keyName;
	}
	public void setKeyName(String keyName){
	 	this.keyName=keyName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
