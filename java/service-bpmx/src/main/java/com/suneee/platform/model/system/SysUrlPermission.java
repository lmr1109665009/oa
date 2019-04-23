package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
/**
 * 对象功能:URL地址拦截管理 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:wdr
 * 创建时间:2014-03-27 16:32:01
 */
public class SysUrlPermission extends BaseModel
{
	// 主键
	protected Long  id;
	// 描述
	protected String  descp;
	// 拦截地址
	protected String  url;
	// 拦截参数
	protected String  params;
	// 是否启用；0：禁用，1：启用
	protected Short  enable;
	public void setId(Long id) 
	{
		this.id = id;
	}
	/**
	 * 返回 主键
	 * @return
	 */
	public Long getId() 
	{
		return this.id;
	}
	public void setDescp(String descp) 
	{
		this.descp = descp;
	}
	/**
	 * 返回 描述
	 * @return
	 */
	public String getDescp() 
	{
		return this.descp;
	}
	public void setUrl(String url) 
	{
		this.url = url;
	}
	/**
	 * 返回 拦截地址
	 * @return
	 */
	public String getUrl() 
	{
		return this.url;
	}
	public void setParams(String params) 
	{
		this.params = params;
	}
	/**
	 * 返回 拦截参数
	 * @return
	 */
	public String getParams() 
	{
		return this.params;
	}
	public void setEnable(Short enable) 
	{
		this.enable = enable;
	}
	/**
	 * 返回 是否启用；0：禁用，1：启用
	 * @return
	 */
	public Short getEnable() 
	{
		return this.enable;
	}

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysUrlPermission)) 
		{
			return false;
		}
		SysUrlPermission rhs = (SysUrlPermission) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.descp) 
		.append(this.url) 
		.append(this.params) 
		.append(this.enable) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("descp", this.descp) 
		.append("url", this.url) 
		.append("params", this.params) 
		.append("enable", this.enable) 
		.toString();
	}
   
  

}