package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.core.model.BaseModel;


/**
 * 对象功能:SYS_ KNOWLEDGE_MARK Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:42
 */
public class SysKnowledgeMark extends BaseModel {
	// ID
	protected Long id;
	// 书签数据
	protected String bookmark;
	
	//用户
	protected Long userId;
	//书签名
	protected String knowName;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
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
	public void setBookmark(String bookmark){
		this.bookmark = bookmark;
	}
	/**
	 * 返回 书签数据
	 * @return
	 */
	public String getBookmark() {
		return this.bookmark;
	}
	
	/**
	 * 返回文章名字
	 * @return
	 */
   	public String getKnowName() {
		return knowName;
	}
	public void setKnowName(String knowName) {
		this.knowName = knowName;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysKnowledgeMark)) 
		{
			return false;
		}
		SysKnowledgeMark rhs = (SysKnowledgeMark) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.bookmark, rhs.bookmark)
		.append(this.userId, rhs.userId)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.bookmark) 
		.append(this.userId)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("bookmark", this.bookmark) 
		.append("userId",this.userId)
		.toString();
	}
   
  

}