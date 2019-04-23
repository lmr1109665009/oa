package com.suneee.eas.oa.model.schedulecalendar;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.core.model.BaseModel;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.utils.DateUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 对象功能:日程表 Model对象
 * 开发公司:深圳象翌微链股份有限公司
 * 开发人员:xiongxianyun
 * 创建时间:2017-06-26 13:36:34
 */
public class ScheduleCalendar extends BaseModel{
	private static final long serialVersionUID = -5783130868041803491L;
	/**
	 *
	 */

	// 日程ID
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long id;
	// 标题
	protected String title;
	// 开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date startTime;
	// 结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date endTime;
	// 事件跳转地址
	protected String url;
	// 备注
	protected String note;
	// 是否全天：0-否，1-是
	protected Short allDay;
	//数据来源id，如：会议id
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long sourceId;

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 日程ID
	 * @return
	 */
	public Long getId() {
		return this.id;
	}
	public void setTitle(String title){
		this.title = title;
	}
	/**
	 * 返回 标题
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	/**
	 * 返回 开始时间
	 * @return
	 */
	public Date getStartTime() {
		return this.startTime;
	}
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	/**
	 * 返回 结束时间
	 * @return
	 */
	public Date getEndTime() {
		return this.endTime;
	}
	public void setUrl(String url){
		this.url = url;
	}
	/**
	 * 返回 事件跳转地址
	 * @return
	 */
	public String getUrl() {
		return this.url;
	}
	public void setNote(String note){
		this.note = note;
	}
	/**
	 * 返回 备注
	 * @return
	 */
	public String getNote() {
		return this.note;
	}
	public void setAllDay(Short allDay){
		this.allDay = allDay;
	}
	/**
	 * 返回 是否全天：0-否，1-是
	 * @return
	 */
	public Short getAllDay() {
		return this.allDay;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getStart(){
		if(startTime != null){
			if(allDay == 1){
				return DateUtil.formatDate(startTime, DateUtil.FORMAT_DATE);
			}else{
				return DateUtil.formatDate(startTime, DateUtil.FORMAT_DATETIME);
			}
		}
		else{
			return "";
		}
	}

	public String getEnd(){
		if(endTime != null){
			if(allDay == 1){
				return DateFormatUtil.format(endTime, DateUtil.FORMAT_DATE);
			}else{
				return DateFormatUtil.format(endTime, DateUtil.FORMAT_DATETIME);
			}
		}
		else{
			return "";
		}
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object)
	{
		if (!(object instanceof ScheduleCalendar))
		{
			return false;
		}
		ScheduleCalendar rhs = (ScheduleCalendar) object;
		return new EqualsBuilder()
				.append(this.id, rhs.id)
				.append(this.title, rhs.title)
				.append(this.startTime, rhs.startTime)
				.append(this.endTime, rhs.endTime)
				.append(this.url, rhs.url)
				.append(this.note, rhs.note)
				.append(this.allDay, rhs.allDay)
				.append(this.createBy, rhs.createBy)
				.append(this.createtime, rhs.createtime)
				.append(this.updateBy, rhs.updateBy)
				.append(this.updatetime, rhs.updatetime)
				.isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return new HashCodeBuilder(-82280557, -700257973)
				.append(this.id)
				.append(this.title)
				.append(this.startTime)
				.append(this.endTime)
				.append(this.url)
				.append(this.note)
				.append(this.allDay)
				.append(this.createBy)
				.append(this.createtime)
				.append(this.updateBy)
				.append(this.updatetime)
				.toHashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("id", this.id)
				.append("title", this.title)
				.append("startTime", this.startTime)
				.append("endTime", this.endTime)
				.append("url", this.url)
				.append("note", this.note)
				.append("allDay", this.allDay)
				.append("createBy", this.createBy)
				.append("createtime", this.createtime)
				.append("updateBy", this.updateBy)
				.append("updatetime", this.updatetime)
				.toString();
	}
}