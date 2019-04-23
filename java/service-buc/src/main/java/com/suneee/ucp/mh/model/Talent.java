package com.suneee.ucp.mh.model;

import com.suneee.ucp.base.model.UcpBaseModel;


/**
 * 招聘筛选
 * @author chengang
 *
 */
public class Talent extends UcpBaseModel{
	/**
	 * 应聘者姓名
	 */
	private String name;
	
	/**
	 * 计划名称
	 */
	private String planname;
	
	/**
	 * 应聘岗位
	 */
	private String post;
	
	
	/**
	 *  电话
	 */
	private String phone;
	
	
	/**
	 * 专业
	 */
	private String professional;
	
	
	/**
	 * 发起人
	 */
	private String originator;
	
	/**
	 * 办理人
	 */
	private String transactor;
	
	
	/**
	 * 筛选时间
	 */
	private String talenttime;
	
	
	/**
	 * 备注
	 */
	private String remark;


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPlanname() {
		return planname;
	}


	public void setPlanname(String planname) {
		this.planname = planname;
	}


	public String getPost() {
		return post;
	}


	public void setPost(String post) {
		this.post = post;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getProfessional() {
		return professional;
	}


	public void setProfessional(String professional) {
		this.professional = professional;
	}


	public String getOriginator() {
		return originator;
	}


	public void setOriginator(String originator) {
		this.originator = originator;
	}


	public String getTransactor() {
		return transactor;
	}


	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}


	public String getTalenttime() {
		return talenttime;
	}


	public void setTalenttime(String talenttime) {
		this.talenttime = talenttime;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}
}
