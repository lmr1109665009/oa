package com.suneee.ucp.me.model;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 办公用品库存修改记录
 * @author yiwei
 *
 */
public class OfficeObjectStorageRecord extends UcpBaseModel{

	private Long id;
	private Long storageId;
	/**
	 * 类型
	 */
	private String type;
	/**
	 * 名称
	 */
	private String objectName;
	
	private String number;
	private String action;
	
	/**
	 * 发布人
	 */
	private String creator;
	
	private int store;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStorageId() {
		return storageId;
	}

	public void setStorageId(Long storageId) {
		this.storageId = storageId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getStore() {
		return store;
	}

	public void setStore(int store) {
		this.store = store;
	}
	
}
