package com.suneee.ucp.me.model;

import java.util.Date;

import com.suneee.core.util.TimeUtil;

/**
 * 
* @ClassName: SysNews 
* @Description: 新闻动态实体
* @author 游刃 
* @date 2017年4月25日 下午2:08:13 
*
 */
public class SysNews {
	private Long id;// 主键
	private String subject;// 主题
	private String content;// 内容
	private String contentTxt;// 内容无格式
	private Long creatorId;// 创建人id
	private String creator;// 创建人
	private Date createTime;// 创建时间
	private String imgUrl;
	
	public String getContentTxt() {
		return contentTxt;
	}
	public void setContentTxt(String contentTxt) {
		this.contentTxt = contentTxt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getCreatetimeStr() {
		return TimeUtil.getDateString(this.createTime);
	}
}
