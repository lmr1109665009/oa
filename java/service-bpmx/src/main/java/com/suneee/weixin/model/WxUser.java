package com.suneee.weixin.model;

import com.alibaba.fastjson.JSONObject;

public class WxUser {
	 private String  userid;
	 private String  name;
	 /*所属部门*/
	 private String []  department;
	 /*岗位*/
	 private String  position;
	 private String  mobile;
	 /*性别 */
	 private String  gender;
	 private String email;
	 private String weixinid;
	 
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getDepartment() {
		return department;
	}
	public void setDepartment(String[] department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWeixinid() {
		return weixinid;
	}
	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}
	@Override
	public String toString() {
		String userJson = JSONObject.toJSONString(this);
		return userJson;
	}
	 
	 
	 
}


