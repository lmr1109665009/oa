package com.suneee.ucp.mh.model.codeTable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;
/**
 * 对象功能:系统码表 Model对象
 * 开发公司:深圳象弈软件有限公司
 * 开发人员:pengfeng
 * 创建时间:2017-08-30 
 */

public class CodeTable extends UcpBaseModel{
	
	/**
	 * 
	 */
	private static final String serialVersionUID = "1";
	
	/**
	 * 设置类型：区域HR负责人
	 */
	public static final String AREA_HR = "1";
	
	/**
	 * 设置类型：部门考勤复核人
	 */
	public static final String DEPART_HR = "2";
	/**
	 * 设置类型：政委
	 */
	public static final String DEPART_ZW = "zw";
	
	
	/**
	 * 设置类型：HRBP
	 */
	public static final String DEPART_HRBP = "HRBP";
	/**
	 * 设置类型：副总经理
	 */
	public static final String DEPART_FZJL = "fzjl";
	/**
	 * 设置类型：总经理
	 */
	public static final String DEPART_ZJL = "zjl";
	/**
	 * 设置类型：分管领导
	 */
	public static final String DEPART_FGLD = "fgld";
	/**
	 * 设置类型：区域行政副总
	 */
	public static final String AREA_MANAGER = "3";
	
	/**
	 * 设置类型：企业地区
	 */
	public static final String COMPANY_AREA = "4";
	
	/**
	 * 设置类型：HR考勤复核人
	 */
	public static final String HR_CHECKER = "5";
	/**
	 * 设置记录ID
	 */
	private Long settingId;
	/**
	 * 设置类型：
	 * 1=区域HR设置，2=部门HR设置，3=地区行政副总设置,4=多企业地区设置,5=
	 */
	private String settingType;
	/**
	 * 设置项ID
	 */
	private String itemId;
	/**
	 *设置项值
	 */
	private String itemValue;
	/**
	 *描述
	 */
	private String description;
	/**
	 * 企业编码
	 */
	private String enterpriseCode;
	
	private String userAccount;
	
	private String userName;
	
	public Long getSettingId() {
		return settingId;
	}
	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public String getSettingType() {
		return settingType;
	}

	public void setSettingType(String settingType) {
		this.settingType = settingType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnterpriseCode(){return enterpriseCode;}

	public void setEnterpriseCode(String enterpriseCode){this.enterpriseCode=enterpriseCode;}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof CodeTable)) 
		{
			return false;
		}
		CodeTable ct = (CodeTable) object;
		return new EqualsBuilder()
		.append(this.settingId, ct.settingId)
		.append(this.settingType,ct.settingType)
		.append(this.itemId,ct.itemId)
		.append(this.itemValue,ct.itemValue)
		.append(this.description,ct.description)
		.append(this.enterpriseCode,ct.enterpriseCode)
		.append(this.userAccount, ct.userAccount)
		.append(this.userName, ct.userName)
		.isEquals();
	}
	
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.settingId) 
		.append(this.settingType) 
		.append(this.itemId) 
		.append(this.itemValue) 
		.append(this.description)
		.append(this.enterpriseCode)
		.append(this.userAccount)
		.append(this.userName)
		.toHashCode();
	}
	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("settingId", this.settingId) 
		.append("settingType", this.settingType) 
		.append("itemId", this.itemId) 
		.append("itemValue", this.itemValue) 
		.append("description", this.description)
		.append("enterpriseCode",enterpriseCode)
		.append("userAccount",this.userAccount)
		.append("userName",this.userName)
		.toString();
	}
}
