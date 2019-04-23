package com.suneee.weixin.model;

import com.alibaba.fastjson.JSONObject;

public class WxOrg {
	private String id;
	private String parentid;
	private String name;
	private String order;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	@Override
	public String toString() {
		String json= JSONObject.toJSONString(this);
		return json;
	}
	
	
}
