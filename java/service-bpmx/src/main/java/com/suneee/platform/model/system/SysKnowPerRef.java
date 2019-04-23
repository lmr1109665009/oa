package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:权限关联主表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-12-31 16:05:42
 */
public class SysKnowPerRef extends BaseModel {
	// ID
	protected Long id;
	// 名称
	protected String name;
	// 创建时间
	protected Date createtime;
	// 创建人ID
	protected Long creatorid;
	//创建人
	protected String creator;
	
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
	 * 返回 名称
	 * @return
	 */
	public String getName() {
		return this.name;
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
	public void setCreatorid(Long creatorid){
		this.creatorid = creatorid;
	}
	/**
	 * 返回 创建人ID
	 * @return
	 */
	public Long getCreatorid() {
		return this.creatorid;
	}
	
   	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysKnowPerRef)) 
		{
			return false;
		}
		SysKnowPerRef rhs = (SysKnowPerRef) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.name, rhs.name)
		.append(this.createtime, rhs.createtime)
		.append(this.creatorid, rhs.creatorid)
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
		.append(this.createtime) 
		.append(this.creatorid) 
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
		.append("createtime", this.createtime) 
		.append("creatorid", this.creatorid) 
		.toString();
	}
   
  

}