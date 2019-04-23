package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import com.suneee.core.util.TimeUtil;

import java.util.Date;

/**
 * 对象功能:公告表 Model对象
 */
public class SysBulletin extends BaseModel {
	// 主键
	protected Long id;
	/**
	 * 主题
	 */
	protected String subject;
	/**
	 * 栏目ID
	 */
	protected Long columnid;
	
	/**
	 * 栏目名称
	 */
	protected String columnname;
	
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
	protected Long creatorid;
	/**
	 * Creator
	 */
	protected String creator;
	
	/**状态 1，已发布；0未发布*/
	protected int status = 1;
	/**是否已读，1已读，0未读*/
	protected int hasRead = 0;

	
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
	
	protected Long tenantid;
	
	/**
	 * 图片地址。
	 */
	protected String imgUrl="";

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

	public void setColumnid(Long columnid) {
		this.columnid = columnid;
	}

	/**
	 * 返回 栏目ID
	 * 
	 * @return
	 */
	public Long getColumnid() {
		return this.columnid;
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

	public void setCreatorid(Long creatorid) {
		this.creatorid = creatorid;
	}

	/**
	 * 返回 creatorId
	 * 
	 * @return
	 */
	public Long getCreatorid() {
		return this.creatorid;
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

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	/**
	 * 返回 createTime
	 * 
	 * @return
	 */
	public Date getCreatetime() {
		return this.createtime;
	}
	
	public String getCreatetimeStr() {
		return TimeUtil.getDateString(this.createtime);
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public int getHasRead() {
		return hasRead;
	}

	public void setHasRead(int hasRead) {
		this.hasRead = hasRead;
	}

	/**
	 * 返回 attachMent
	 * 
	 * @return
	 */
	public String getAttachment() {
		return this.attachment;
	}

	public String getColumnname() {
		return columnname;
	}

	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}

	public Long getTenantid() {
		return tenantid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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