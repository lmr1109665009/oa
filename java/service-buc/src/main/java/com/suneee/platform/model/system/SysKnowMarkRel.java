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
 * 对象功能:SYS_KNOW_MARK_REL Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:dyg
 * 创建时间:2015-12-31 15:26:01
 */
public class SysKnowMarkRel extends BaseModel {
	// ID
	protected Long id;
	// 文章id
	protected Long knowledgeid;
	// 书签id
	protected Long markid;

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
	public void setKnowledgeid(Long knowledgeid){
		this.knowledgeid = knowledgeid;
	}
	/**
	 * 返回 文章id
	 * @return
	 */
	public Long getKnowledgeid() {
		return this.knowledgeid;
	}
	public void setMarkid(Long markid){
		this.markid = markid;
	}
	/**
	 * 返回 书签id
	 * @return
	 */
	public Long getMarkid() {
		return this.markid;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysKnowMarkRel)) 
		{
			return false;
		}
		SysKnowMarkRel rhs = (SysKnowMarkRel) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.knowledgeid, rhs.knowledgeid)
		.append(this.markid, rhs.markid)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.knowledgeid) 
		.append(this.markid) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("knowledgeid", this.knowledgeid) 
		.append("markid", this.markid) 
		.toString();
	}
   
  

}