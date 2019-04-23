/**
 * @Title: AccessRecord.java 
 * @Package com.suneee.eas.oa.model.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.model.conference;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;

/**
 * @ClassName: AccessRecord 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 10:35:01 
 *
 */
public class AccessRecord {
	public static final String TARGET_TYPE_CONFERENCE = "conference-reserve";
	public static final String TARGET_TYPE_CONFERENCE_NOTE = "conference-note";
	/**
	 * 访问记录ID 
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long accessId;
	/**
	 * 访问对象ID
	 */ 
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	private Long targetId;
	/**
	 * 访问对象类型 
	 */ 
	private String targetType;
	/**
	 * 访问者ID
	 */ 
	private Long accessor;
	/**
	 * 访问者姓名
	 */ 
	private String accessorName;
	/**
	 * 访问时间 
	 */ 
	private Date accessTime;
	// 访问次数，用于前端展示
	private Long accessCount;
	/**
	 * @return the accessId
	 */
	public Long getAccessId() {
		return accessId;
	}
	/**
	 * @param accessId the accessId to set
	 */
	public void setAccessId(Long accessId) {
		this.accessId = accessId;
	}
	/**
	 * @return the targetId
	 */
	public Long getTargetId() {
		return targetId;
	}
	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	/**
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}
	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	/**
	 * @return the accessor
	 */
	public Long getAccessor() {
		return accessor;
	}
	/**
	 * @param accessor the accessor to set
	 */
	public void setAccessor(Long accessor) {
		this.accessor = accessor;
	}
	/**
	 * @return the accessorName
	 */
	public String getAccessorName() {
		return accessorName;
	}
	/**
	 * @param accessorName the accessorName to set
	 */
	public void setAccessorName(String accessorName) {
		this.accessorName = accessorName;
	}
	/**
	 * @return the accessTime
	 */
	public Date getAccessTime() {
		return accessTime;
	}
	/**
	 * @param accessTime the accessTime to set
	 */
	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}
	/**
	 * @return the accessCount
	 */
	public Long getAccessCount() {
		return accessCount;
	}
	/**
	 * @param accessCount the accessCount to set
	 */
	public void setAccessCount(Long accessCount) {
		this.accessCount = accessCount;
	}
	/** (non-Javadoc)
	 * @Title: toString 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @return 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [accessId=");
		builder.append(accessId);
		builder.append(", targetId=");
		builder.append(targetId);
		builder.append(", targetType=");
		builder.append(targetType);
		builder.append(", accessor=");
		builder.append(accessor);
		builder.append(", accessorName=");
		builder.append(accessorName);
		builder.append(", accessTime=");
		builder.append(accessTime);
		builder.append("]");
		return builder.toString();
	}
}
