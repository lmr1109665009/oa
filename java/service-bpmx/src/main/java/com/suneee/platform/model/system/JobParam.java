package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;


/**
 * 对象功能:职务参数 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-09-23 11:51:14
 */
public class JobParam extends BaseModel {
	// 主键
	protected Long id;
	// 职务ID
	protected Long jobid;
	// 键值对KEY
	protected String key;
	// 键值对VALUE
	protected String value;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 主键
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setJobid(Long jobid){
		this.jobid = jobid;
	}
	/**
	 * 返回 职务ID
	 * @return
	 */
	public Long getJobid() {
		return this.jobid;
	}
	public void setKey(String key){
		this.key = key;
	}
	/**
	 * 返回 键值对KEY
	 * @return
	 */
	public String getKey() {
		return this.key;
	}
	public void setValue(String value){
		this.value = value;
	}
	/**
	 * 返回 键值对VALUE
	 * @return
	 */
	public String getValue() {
		return this.value;
	}

}