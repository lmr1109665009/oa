package com.suneee.eas.oa.model.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.suneee.core.util.TimeUtil;
import com.suneee.eas.common.component.jackson.LongJsonDeserializer;
import com.suneee.eas.common.component.jackson.LongJsonSerializer;
import com.suneee.eas.common.model.BaseModel;

import java.util.Date;

/**
 * 对象功能:公告表 Model对象
 */
public class SysBulletin extends BaseModel {
	// 主键
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long id;
	/**
	 * 主题
	 */
	protected String subject;
	/**
	 * 公告分类ID
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long columnId;

	/**
	 * 栏目名称
	 */
	protected String columnName;
	
	/**
	 * 内容
	 */
	protected String content;
	/**
	 * 内容无格式
	 */
	protected String contentTxt;
	/**
	 * creatorId
	 */
	@JsonSerialize(using = LongJsonSerializer.class)
	@JsonDeserialize(using = LongJsonDeserializer.class)
	protected Long creatorId;
	/**
	 * Creator
	 */
	protected String creator;
	
	/**状态 1，已发布；0未发布*/
	protected int status = 1;

	public String getContentTxt() {
		return contentTxt;
	}

	public void setContentTxt(String contentTxt) {
		this.contentTxt = contentTxt;
	}

	/**	
	 * attachMent
	 */
	protected String attachment;

	//发布部门
	protected Long publishOrg;
	//发布范围id_人员
	protected String publishRangeID_user;
	//发布范围id_组织
	protected String publishRangeID_org;
	//发布范围名称
	protected String publishRangeName;
	//发布时间
	protected Date publishTime;
	//关键词
	protected String keyWords;
	//所属企业
	protected String enterpriseCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * 返回 主题
	 * 
	 * @return
	 */
	public String getSubject() {
		return this.subject;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	/**
	 * 返回 栏目ID
	 * 
	 * @return
	 */
	public Long getColumnId() {
		return this.columnId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 返回 内容
	 * 
	 * @return
	 */
	public String getContent() {
		return this.content;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * 返回 creatorId
	 * 
	 * @return
	 */
	public Long getCreatorId() {
		return this.creatorId;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 返回 Creator
	 * 
	 * @return
	 */
	public String getCreator() {
		return this.creator;
	}

	public String getCreatetimeStr() {
		return TimeUtil.getDateString(this.createTime);
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	/**
	 * 返回 attachMent
	 * 
	 * @return
	 */
	public String getAttachment() {
		return this.attachment;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getPublishOrg() {
		return publishOrg;
	}

	public void setPublishOrg(Long publishOrg) {
		this.publishOrg = publishOrg;
	}

	public String getPublishRangeID_user() {
		return publishRangeID_user;
	}

	public void setPublishRangeID_user(String publishRangeID_user) {
		this.publishRangeID_user = publishRangeID_user;
	}

	public String getPublishRangeID_org() {
		return publishRangeID_org;
	}

	public void setPublishRangeID_org(String publishRangeID_org) {
		this.publishRangeID_org = publishRangeID_org;
	}

	public String getPublishRangeName() {
		return publishRangeName;
	}

	public void setPublishRangeName(String publishRangeName) {
		this.publishRangeName = publishRangeName;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}
}