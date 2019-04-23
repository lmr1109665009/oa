package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;


/**
 * 对象功能:SYS_ KNOWLEDGE _PERMISSION Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:07
 */
public class SysKnowledgePer extends BaseModel {
	// ID
	protected Long id;
	// 分类id
	protected Long typeId;
	// 分类名称
	protected String typeName;
	protected String permissionType;
	// 权限ID
	protected Long ownerId;
	//所有人
	protected String owner;
	// 权限类型 org,user,role
	protected String permissionJson;
	//用户、组织的list
	protected List<String> typeUserList;
	
	protected Long refId;
	
	public void setId(Long id){
		this.id = id;
	}

	
	
	public Long getTypeId() {
		return typeId;
	}



	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}



	public String getTypeName() {
		return typeName;
	}



	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}



	public String getPermissionType() {
		return permissionType;
	}



	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}



	public Long getOwnerId() {
		return ownerId;
	}



	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}



	public String getOwner() {
		return owner;
	}



	public void setOwner(String owner) {
		this.owner = owner;
	}



	public String getPermissionJson() {
		return permissionJson;
	}



	public void setPermissionJson(String permissionJson) {
		this.permissionJson = permissionJson;
	}


	public List<String> getTypeUserList() {
		return typeUserList;
	}



	public void setTypeUserList(List<String> typeUserList) {
		this.typeUserList = typeUserList;
	}



	public Long getId() {
		return id;
	}



	public Long getRefId() {
		return refId;
	}



	public void setRefId(Long refId) {
		this.refId = refId;
	}



	public boolean equals(Object object) 
	{
		if (!(object instanceof SysKnowledgePer)) 
		{
			return false;
		}
		SysKnowledgePer rhs = (SysKnowledgePer) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.typeId, rhs.typeId)
		.append(this.permissionType, rhs.permissionType)
		.append(this.ownerId, rhs.ownerId)
		.append(this.owner, rhs.owner)
		.append(this.permissionJson, rhs.permissionJson)
		.append(this.refId, rhs.refId)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.typeId) 
		.append(this.permissionType) 
		.append(this.ownerId)
		.append(this.owner) 
		.append(this.permissionJson) 
		.append(this.refId)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("typeTd", this.typeId) 
		.append("permissionType", this.permissionType) 
		.append("ownerId", this.ownerId) 
		.append("owner", this.owner)
		.append("permissionJson", this.permissionJson) 
		.append("refId",this.refId)
		.toString();
	}
   
  

}