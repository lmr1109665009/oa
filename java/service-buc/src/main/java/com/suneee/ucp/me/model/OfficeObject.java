package com.suneee.ucp.me.model;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 办公用品
 * @author yiwei
 *
 */
public class OfficeObject extends UcpBaseModel{

	private Long id;
	/**
	 * 类型
	 */
	private String type;
	/**
	 * 名称
	 */
	private String objectName;
	/**
	 * 规格
	 */
	private String specification;
	/**
	 * 价格
	 */
	private String area;
	
	/**
	 * 发布人
	 */
	private String creator;
	/**
	 * 库存
	 */
	private int store;
	
	private String remark;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
