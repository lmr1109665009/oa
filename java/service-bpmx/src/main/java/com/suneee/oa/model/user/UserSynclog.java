/**
 * 
 */
package com.suneee.oa.model.user;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.suneee.ucp.base.model.UcpBaseModel;

/**
 * @ClassName: UserSynclog 
 * @Description: 用户同步日志对象
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-01-04 10:55:03 
 *
 */
public class UserSynclog extends UcpBaseModel{
	/**
	 * @Fields serialVersionUID
	 */ 
	private static final long serialVersionUID = -339128529443661051L;
	
	/*操作类型枚举值*/
	/** 添加企业B用户**/
	public static final Short OPTYPE_ADD_USER = 1;
	/** 更新用户信息**/
	public static final Short OPTYPE_UPD_USER = 2;
	/** 删除用户信息**/
	public static final Short OPTYPE_DEL_USER = 3;
	/** 恢复用户信息**/
	public static final Short OPTYPE_REV_USER = 4;
	/** 更新用户企业关系**/
	public static final Short OPTYPE_UPD_USER_ORG = 5;
	/** 删除用户企业关系**/
	public static final Short OPTYPE_DEL_USER_ORG = 6;
	/** 用户鉴权**/
	public static final Short OPTYPE_AUTH_USER = 7;
	
	/**
	 * 日志ID
	 */
	private Long logId;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 用户姓名
	 */ 
	private String fullname;

	/**
	 * 操作类型
	 */
	private Short opType;
	
	/**
	 * 错误信息
	 */
	private String errorInfo;
	
	/**
	 * 备注
	 */
	private String note;

	/**
	 * @return the logId
	 */
	public Long getLogId() {
		return logId;
	}

	/**
	 * @param logId the logId to set
	 */
	public void setLogId(Long logId) {
		this.logId = logId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @param fullname the fullname to set
	 */
	public void setFullname(String fullname) {
		
		this.fullname = fullname;
	}

	/**
	 * @return the opType
	 */
	public Short getOpType() {
		return opType;
	}

	/**
	 * @param opType the opType to set
	 */
	public void setOpType(Short opType) {
		this.opType = opType;
	}

	/**
	 * @return the errorInfo
	 */
	public String getErrorInfo() {
		return errorInfo;
	}

	/**
	 * @param errorInfo the errorInfo to set
	 */
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) 
	{
		if (!(object instanceof UserSynclog)) 
		{
			return false;
		}
		UserSynclog rhs = (UserSynclog) object;
		return new EqualsBuilder()
		.append(this.logId, rhs.logId)
		.append(this.userId, rhs.userId)
		.append(this.opType, rhs.opType)
		.append(this.errorInfo, rhs.errorInfo)
		.append(this.note, rhs.note)
		.append(this.createtime, rhs.createtime)
		.append(this.createBy, rhs.createBy)
		.append(this.updatetime, rhs.updatetime)
		.append(this.updateBy, rhs.updateBy)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() 
	{
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.logId) 
		.append(this.userId) 
		.append(this.opType)
		.append(this.errorInfo)
		.append(this.note) 
		.append(this.createtime) 
		.append(this.createBy) 
		.append(this.updatetime) 
		.append(this.updateBy) 
		.toHashCode();
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this)
		.append("logId", this.logId)
		.append("userId", this.userId)
		.append("opType", this.opType)
		.append("errorInfo", this.errorInfo)
		.append("note", this.note)
		.append("createBy", this.createBy)
		.append("createtime", this.createtime)
		.append("updateBy", this.updateBy)
		.append("updatetime", this.updatetime)
		.toString();
	}
	
}
