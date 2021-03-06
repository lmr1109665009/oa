package com.suneee.platform.model.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.core.model.BaseModel;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 对象功能:用户角色映射表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-12-16 15:47:55
 */
public class UserRole extends BaseModel implements Cloneable
{
	
	// 用户角色Id
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long userRoleId;
	
	// 角色ID
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long roleId;
	// 用户ID
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long userId;
	// 姓名
	protected String fullname;
	// 帐号
	protected String account;
	//角色名称
	protected String roleName="";
	
	//系统名称
	protected String systemName="";
	

	
	
	

	public void setRoleId(Long roleId) 
	{
		this.roleId = roleId;
	}
	/**
	 * 返回 角色ID
	 * @return
	 */
	public Long getRoleId() 
	{
		return roleId;
	}

	public void setUserId(Long userId) 
	{
		this.userId = userId;
	}
	/**
	 * 返回 用户ID
	 * @return
	 */
	public Long getUserId() 
	{
		return userId;
	}

	public void setUserRoleId(Long userRoleId) 
	{
		this.userRoleId = userRoleId;
	}
	/**
	 * 返回 用户角色Id
	 * @return
	 */
	public Long getUserRoleId() 
	{
		return userRoleId;
	}
	
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

   
   	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof UserRole)) 
		{
			return false;
		}
		UserRole rhs = (UserRole) object;
		return new EqualsBuilder()
		.append(this.roleId, rhs.roleId)
		.append(this.userId, rhs.userId)
		
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.roleId) 
		.append(this.userId) 
		
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("roleId", this.roleId) 
		.append("userId", this.userId) 
		.toString();
	}
   
	
	public Object clone()
	{
		UserRole obj=null;
		try{
			obj=(UserRole)super.clone();
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
  

}