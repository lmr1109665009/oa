package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 对象功能:系统角色表 Model对象 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2011-11-18 16:24:10
 */
public class SysRole extends BaseModel implements GrantedAuthority,Cloneable
{
	private final static String ROLE_SUPER  = "ROLE_SUPER";//超级
	private final static String ROLE_PUBLIC  = "ROLE_PUBLIC";//公共角色
	private final static String ROLE_ANONYMOUS  = "ROLE_ANONYMOUS";//匿名级
	
	public final static GrantedAuthority ROLE_GRANT_SUPER=new SimpleGrantedAuthority(SysRole.ROLE_SUPER);
	public final static ConfigAttribute  ROLE_CONFIG_PUBLIC=new SecurityConfig(SysRole.ROLE_PUBLIC);
	public final static ConfigAttribute  ROLE_CONFIG_ANONYMOUS=new SecurityConfig(SysRole.ROLE_ANONYMOUS);
	
	
	
	// roleId
	protected Long roleId;
	// 系统ID  如果为0 则为全局角色
	protected Long systemId;
	// 角色别名
	protected String alias;
	// 角色名
	protected String roleName;
	// 备注
	protected String memo;
	// 允许删除
	protected Short allowDel;
	// 允许编辑
	protected Short allowEdit;
	// 是否启用
	protected Short enabled;
	//子系统
	protected SubSystem subSystem;
	
	protected String systemName="";
	//是否父节点
	protected String isParent="false";
	
	//角色分类
	protected String category;
	
	/**
	 * 所属企业
	 */ 
	protected String enterpriseCode;
	
	public void setRoleId(Long roleId)
	{
		this.roleId = roleId;
	}

	/**
	 * 返回 roleId
	 * 
	 * @return
	 */
	public Long getRoleId()
	{
		return roleId;
	}

	public void setSystemId(Long systemId)
	{
		this.systemId = systemId;
	}

	/**
	 * 返回 系统ID
	 * 
	 * @return
	 */
	public Long getSystemId()
	{
		return systemId;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	/**
	 * 返回 角色别名
	 * 
	 * @return
	 */
	public String getAlias()
	{
		return alias;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	/**
	 * 返回 角色名
	 * 
	 * @return
	 */
	public String getRoleName()
	{
		return roleName;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	/**
	 * 返回 备注
	 * 
	 * @return
	 */
	public String getMemo()
	{
		return memo;
	}

	public void setAllowDel(Short allowDel)
	{
		this.allowDel = allowDel;
	}

	/**
	 * 返回 允许删除
	 * 
	 * @return
	 */
	public Short getAllowDel()
	{
		return allowDel;
	}

	public void setAllowEdit(Short allowEdit)
	{
		this.allowEdit = allowEdit;
	}

	/**
	 * 返回 允许编辑
	 * 
	 * @return
	 */
	public Short getAllowEdit()
	{
		return allowEdit;
	}

	public void setEnabled(Short enabled)
	{
		this.enabled = enabled;
	}
	
	public SubSystem getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(SubSystem subSystem) {
		this.subSystem = subSystem;
	}


	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * 返回 是否启用
	 * 
	 * @return
	 */
	public Short getEnabled()
	{
		return enabled;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the enterpriseCode
	 */
	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	/**
	 * @param enterpriseCode the enterpriseCode to set
	 */
	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object)
	{
		if (!(object instanceof SysRole))
		{
			return false;
		}
		SysRole rhs = (SysRole) object;
		return new EqualsBuilder()
				.append(this.alias, rhs.alias)
				.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode()
	{
		return new HashCodeBuilder(-82280557, -700257973)
				.append(this.roleId)
				.append(this.systemId)
				.append(this.alias)
				.append(this.roleName)
				.append(this.memo)
				.append(this.allowDel)
				.append(this.allowEdit)
				.append(this.enabled)
				.append(this.category)
				.append(this.enterpriseCode)
				.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("roleId", this.roleId)
				.append("systemId", this.systemId)
				.append("alias", this.alias)
				.append("roleName", this.roleName)
				.append("memo", this.memo)
				.append("allowDel", this.allowDel)
				.append("allowEdit", this.allowEdit)
				.append("enabled", this.enabled)
				.append("category", this.category)
				.append("enterpriseCode", this.enterpriseCode)
				.toString();
	}
	
	public Object clone()
	{
		SysRole obj=null;
		try{
			obj=(SysRole)super.clone();
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public String getAuthority()
	{
		return alias;
	}

//	@Override
//	public void setIsParent(String isParent) {
//		// TODO Auto-generated method stub
//		this.isParent=isParent;
//	}
//
//	@Override
//	public String getIsParent() {
//		// TODO Auto-generated method stub
//		return isParent;
//	}
}