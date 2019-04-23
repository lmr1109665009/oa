package com.suneee.platform.model.system;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * 对象功能:知识库 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:15:59
 */
public class SysKnowledge {
	// ID
	protected Long id;
	// 类型id
	protected Long typeid;
	//分类名称
	protected String typeName;
	// 主题
	protected String subject;
	// 内容
	protected String content;
	// 创建人id
	protected Long creatorid;
	// 创建人
	protected String creator;
	// 创建时间
	protected Date createtime;
	// 附件
	protected String attachment;
	// 更新人id
	protected Long updatorid;
	// 更新人
	protected String updator;
	// 更新时间
	protected Date updatetime;
	
	
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
	public void setTypeid(Long typeid){
		this.typeid = typeid;
	}
	
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * 返回 类型id
	 * @return
	 */
	public Long getTypeid() {
		return this.typeid;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	/**
	 * 返回 主题
	 * @return
	 */
	public String getSubject() {
		return this.subject;
	}
	public void setContent(String content){
		this.content = content;
	}
	/**
	 * 返回 内容
	 * @return
	 */
	public String getContent() {
		return this.content;
	}
	public void setCreatorid(Long creatorid){
		this.creatorid = creatorid;
	}
	/**
	 * 返回 创建人id
	 * @return
	 */
	public Long getCreatorid() {
		return this.creatorid;
	}
	public void setCreator(String creator){
		this.creator = creator;
	}
	/**
	 * 返回 创建人
	 * @return
	 */
	public String getCreator() {
		return this.creator;
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
	public void setAttachment(String attachment){
		this.attachment = attachment;
	}
	/**
	 * 返回 附件
	 * @return
	 */
	public String getAttachment() {
		return this.attachment;
	}
	public void setUpdatorid(Long updatorid){
		this.updatorid = updatorid;
	}
	/**
	 * 返回 更新人id
	 * @return
	 */
	public Long getUpdatorid() {
		return this.updatorid;
	}
	public void setUpdator(String updator){
		this.updator = updator;
	}
	/**
	 * 返回 更新人
	 * @return
	 */
	public String getUpdator() {
		return this.updator;
	}
	public void setUpdatetime(Date updatetime){
		this.updatetime = updatetime;
	}
	/**
	 * 返回 更新时间
	 * @return
	 */
	public Date getUpdatetime() {
		return this.updatetime;
	}
	
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysKnowledge)) 
		{
			return false;
		}
		SysKnowledge rhs = (SysKnowledge) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.typeid, rhs.typeid)
		.append(this.subject, rhs.subject)
		.append(this.content, rhs.content)
		.append(this.creatorid, rhs.creatorid)
		.append(this.creator, rhs.creator)
		.append(this.createtime, rhs.createtime)
		.append(this.attachment, rhs.attachment)
		.append(this.updatorid, rhs.updatorid)
		.append(this.updator, rhs.updator)
		.append(this.updatetime, rhs.updatetime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.typeid) 
		.append(this.subject) 
		.append(this.content) 
		.append(this.creatorid) 
		.append(this.creator) 
		.append(this.createtime) 
		.append(this.attachment) 
		.append(this.updatorid) 
		.append(this.updator) 
		.append(this.updatetime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("typeid", this.typeid) 
		.append("subject", this.subject) 
		.append("content", this.content) 
		.append("creatorid", this.creatorid) 
		.append("creator", this.creator) 
		.append("createtime", this.createtime) 
		.append("attachment", this.attachment) 
		.append("updatorid", this.updatorid) 
		.append("updator", this.updator) 
		.append("updatetime", this.updatetime) 
		.toString();
	}
   
  

}