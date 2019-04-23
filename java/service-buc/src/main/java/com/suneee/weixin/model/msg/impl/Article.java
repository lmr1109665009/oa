package com.suneee.weixin.model.msg.impl;

/**
 * 文章。
 * <pre>
 * {
 * "title": "Title",
   "description": "Description",
   "url": "URL",
   "picurl": "PIC_URL"
   }
   </pre>
 * @author ray
 *
 */
public class Article {
	
	
	/**
	 * 标题。
	 */
	private String title="";
	/**
	 * 描述。
	 */
	private String description="";
	/**
	 * 地址。
	 */
	private String url="";
	
	/**
	 * 地址。
	 */
	private String picurl="";
	
	public Article(){}
	
	public Article(String title,String desc,String url,String picurl){
		this.title=title;
		this.description=desc;
		this.url=url;
		this.picurl=picurl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	
	
	

}
