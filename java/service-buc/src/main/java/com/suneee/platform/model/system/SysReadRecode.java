package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import com.suneee.core.util.UniqueIdUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * 对象功能:已读记录 Model对象 例如公告已读
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miaojf
 * 创建时间:2015-07-13 18:44:29
 */
public class SysReadRecode extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ID
	protected Long id;
	// 来源；eg:公告：BULLETINID,
	protected String source;
	// 分组ID；删除时可以删除一组，如存放流程实例id
	protected Long groupid;
	// 对象ID
	protected Long objectid;
	// 用户ID
	protected Long userid;
	// 阅读时间
	protected Date createtime;

	public SysReadRecode(Long objectId, Long userId, String source, Long groupId) {
		this.id = UniqueIdUtil.genId();
		this.source = source;
		this.groupid = groupId;
		this.objectid = objectId;
		this.userid = userId;
		this.createtime = new Date();
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
	public void setSource(String source){
		this.source = source;
	}
	/**
	 * 返回 来源；eg:公告：BULLETINID
	 * @return
	 */
	public String getSource() {
		return this.source;
	}
	public void setGroupid(Long groupid){
		this.groupid = groupid;
	}
	/**
	 * 返回 分组ID；删除时可以删除一组，如存放流程实例id
	 * @return
	 */
	public Long getGroupid() {
		return this.groupid;
	}
	public void setObjectid(Long objectid){
		this.objectid = objectid;
	}
	/**
	 * 返回 对象ID
	 * @return
	 */
	public Long getObjectid() {
		return this.objectid;
	}
	public void setUserid(Long userid){
		this.userid = userid;
	}
	/**
	 * 返回 用户ID
	 * @return
	 */
	public Long getUserid() {
		return this.userid;
	}
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 阅读时间
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	

   	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof SysReadRecode)) 
		{
			return false;
		}
		SysReadRecode rhs = (SysReadRecode) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.source, rhs.source)
		.append(this.groupid, rhs.groupid)
		.append(this.objectid, rhs.objectid)
		.append(this.userid, rhs.userid)
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
		.append(this.source) 
		.append(this.groupid) 
		.append(this.objectid) 
		.append(this.userid) 
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
		.append("source", this.source) 
		.append("groupid", this.groupid) 
		.append("objectid", this.objectid) 
		.append("userid", this.userid) 
		.append("createtime", this.createtime) 
		.toString();
	}
   
  

}