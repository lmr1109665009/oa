package com.suneee.platform.model.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.suneee.core.model.BaseModel;
import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;


/**
 * 对象功能:常用联系人组 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-07-29 10:29:57
 */
public class MessageLinkmanGroup extends BaseModel {
	// 主键
	protected Long id;
	// 组名
	protected String groupName;
	// 常用联系人ID
	protected String userIds;
	// 常用联系人
	protected String users;
	// 创建人
	protected Long creatorId;
	// 创建时间
	protected Date createTime;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 主键
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setGroupName(String groupName){
		this.groupName = groupName;
	}
	/**
	 * 返回 组名
	 * @return
	 */
	public String getGroupName() {
		return this.groupName;
	}
	public void setUserIds(String userIds){
		this.userIds = userIds;
	}
	/**
	 * 返回 常用联系人ID
	 * @return
	 */
	public String getUserIds() {
		return this.userIds;
	}
	public void setUsers(String users){
		this.users = users;
	}
	/**
	 * 返回 常用联系人
	 * @return
	 */
	public String getUsers() {
		return this.users;
	}
	public void setCreatorId(Long creatorId){
		this.creatorId = creatorId;
	}
	/**
	 * 返回 创建人
	 * @return
	 */
	public Long getCreatorId() {
		return this.creatorId;
	}
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	/**
	 * 返回 创建时间
	 * @return
	 */
	public Date getCreateTime() {
		return this.createTime;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof MessageLinkmanGroup)) 
		{
			return false;
		}
		MessageLinkmanGroup rhs = (MessageLinkmanGroup) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.groupName, rhs.groupName)
		.append(this.userIds, rhs.userIds)
		.append(this.users, rhs.users)
		.append(this.creatorId, rhs.creatorId)
		.append(this.createTime, rhs.createTime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.groupName) 
		.append(this.userIds) 
		.append(this.users) 
		.append(this.creatorId) 
		.append(this.createTime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("groupName", this.groupName) 
		.append("userIds", this.userIds) 
		.append("users", this.users) 
		.append("creatorId", this.creatorId) 
		.append("createTime", this.createTime) 
		.toString();
	}
   
  

}