package com.suneee.weixin.model;

public class RowModel {
	
	/**
	 * 标题
	 */
	private String title="";
	
	/**
	 * 主键
	 */
	private String id="";
	
	/**
	 * 创建日期
	 */
	private String date="";
	
	
	private String desc="";
	
	public RowModel(){}

	public RowModel(String title, String id, String date) {
		this.title = title;
		this.id = id;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	

}
