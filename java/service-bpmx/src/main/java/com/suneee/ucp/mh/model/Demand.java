package com.suneee.ucp.mh.model;

import com.suneee.ucp.base.model.UcpBaseModel;

public class Demand extends UcpBaseModel{
	/**
	 * 需求ID
	 */
	private String demandId;
	
	/**
	 * 需求岗位
	 */
	private String jobsname;
	
	/**
	 * 用工日期
	 */
	private String usetime;
	
	
	/**
	 *  需求部门
	 */
	private String department;
	
	
	/**
	 * 招聘人数
	 */
	private String number;
	
	
	/**
	 * 附件
	 */
	private String attachment;
	
	/**
	 * 岗位要求
	 */
	private String demand;
	
	
	/**
	 * 备注
	 */
	private String remark;
	
	
	/**
	 * 申请人
	 */
	private String createby;


	public String getDemandId() {
		return demandId;
	}


	public void setDemandId(String demandId) {
		this.demandId = demandId;
	}


	public String getJobsname() {
		return jobsname;
	}


	public void setJobsname(String jobsname) {
		this.jobsname = jobsname;
	}


	public String getUsetime() {
		return usetime;
	}


	public void setUsetime(String usetime) {
		this.usetime = usetime;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	public String getAttachment() {
		return attachment;
	}


	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}


	public String getDemand() {
		return demand;
	}


	public void setDemand(String demand) {
		this.demand = demand;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getCreateby() {
		return createby;
	}


	public void setCreateby(String createby) {
		this.createby = createby;
	}
	
}
