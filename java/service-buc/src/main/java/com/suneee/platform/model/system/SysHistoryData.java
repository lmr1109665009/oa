package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:历史数据 Model对象
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-06-26 22:47:29
 */
public class SysHistoryData extends BaseModel {
	//自定义SQL查询历史。
	public static String SYS_QUERY_VIEW_TEMPLATE="queryViewTemplate";
	//表单历史
	public static String SYS_FORMDEF_TEMPLATE="formDefTemplate";
	
	// 主键
	protected Long id;
	// 类型
	protected String type;
	
	protected String subject="";
	// 关联对象ID
	protected Long objId;
	// 内容
	protected String content;
	// 创建人
	protected String creator;
	// 创建时间
	protected Date createtime;

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
	public void setType(String type){
		this.type = type;
	}
	/**
	 * 返回 类型
	 * @return
	 */
	public String getType() {
		return this.type;
	}
	public void setObjId(Long objId){
		this.objId = objId;
	}
	/**
	 * 返回 关联对象ID
	 * @return
	 */
	public Long getObjId() {
		return this.objId;
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
	

   	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysHistoryData)) 
		{
			return false;
		}
		SysHistoryData rhs = (SysHistoryData) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.type, rhs.type)
		.append(this.objId, rhs.objId)
		.append(this.content, rhs.content)
		.append(this.creator, rhs.creator)
		.append(this.createtime, rhs.createtime)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id) 
		.append(this.type) 
		.append(this.objId) 
		.append(this.content) 
		.append(this.creator) 
		.append(this.createtime) 
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() 
	{
		return new ToStringBuilder(this)
		.append("id", this.id) 
		.append("type", this.type) 
		.append("objId", this.objId) 
		.append("content", this.content) 
		.append("creator", this.creator) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}