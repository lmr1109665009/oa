package com.suneee.ucp.me.model;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * 办公用品申请
 * @author yiwei
 *
 */
public class OfficeObjectApply extends UcpBaseModel{

	private long id;
	private Long processId;
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
	 * 申请数量
	 */
	private int applyNumber;
	/**
	 * 价格
	 */
	private String area;
	/**
	 * 申请人
	 */
	private String creator;
	
	/**
	 * 审核人
	 */
	private String approver;
	
	/**
	 * 审核人id
	 */
	private String approverId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
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

	public int getApplyNumber() {
		return applyNumber;
	}

	public void setApplyNumber(int applyNumber) {
		this.applyNumber = applyNumber;
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

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	
	
	
	
	
}
