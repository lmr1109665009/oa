package com.suneee.platform.model.oa;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:联系人 model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-14 09:13:57
 */
public class OaLinkman extends BaseModel {
	// ID
	protected Long id;
	// NAME
	protected String name;
	// 性别
	protected String sex;
	// 电话
	protected String phone;
	// 邮箱
	protected String email;
	// 公司
	protected String company;
	// 工作
	protected String job;
	// 地址
	protected String address;
	// 创建时间
	protected Date createtime;
	//状态
	protected int status;

	//创建人id
	protected Long userid;
	
	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 ID
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 NAME
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	public void setSex(String sex){
		this.sex = sex;
	}
	/**
	 * 返回 性别
	 * @return
	 */
	public String getSex() {
		return this.sex;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	/**
	 * 返回 电话
	 * @return
	 */
	public String getPhone() {
		return this.phone;
	}
	public void setEmail(String email){
		this.email = email;
	}
	/**
	 * 返回 邮箱
	 * @return
	 */
	public String getEmail() {
		return this.email;
	}
	public void setCompany(String company){
		this.company = company;
	}
	/**
	 * 返回 公司
	 * @return
	 */
	public String getCompany() {
		return this.company;
	}
	public void setJob(String job){
		this.job = job;
	}
	/**
	 * 返回 工作
	 * @return
	 */
	public String getJob() {
		return this.job;
	}
	public void setAddress(String address){
		this.address = address;
	}
	/**
	 * 返回 地址
	 * @return
	 */
	public String getAddress() {
		return this.address;
	}
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 创建时间
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	/**
	 * 状态1为启用0为禁用
	 * @return
	 */
   	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof OaLinkman)) 
		{
			return false;
		}
		OaLinkman rhs = (OaLinkman) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.name, rhs.name)
		.append(this.sex, rhs.sex)
		.append(this.phone, rhs.phone)
		.append(this.email, rhs.email)
		.append(this.company, rhs.company)
		.append(this.job, rhs.job)
		.append(this.address, rhs.address)
		.append(this.createtime, rhs.createtime)
		.append(this.status, rhs.status)
		.append(this.userid, rhs.userid)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.name) 
		.append(this.sex) 
		.append(this.phone) 
		.append(this.email) 
		.append(this.company) 
		.append(this.job) 
		.append(this.address) 
		.append(this.createtime) 
		.append(this.status)
		.append(this.userid)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("name", this.name) 
		.append("sex", this.sex) 
		.append("phone", this.phone) 
		.append("email", this.email) 
		.append("company", this.company) 
		.append("job", this.job) 
		.append("address", this.address) 
		.append("createtime", this.createtime) 
		.append("status",this.status)
		.append("userid",this.userid)
		.toString();
	}
   
  

}