package com.suneee.weixin.model;

import java.util.Date;

/**
 * token对象。
 * @author ray
 *
 */
public class TokenModel {
	
	/**
	 * 最后更新时间。
	 */
	private Date lastUpdTime=new Date();
	
	/**
	 * accesstoken 数据。
	 */
	private String token="";
	
	/**
	 * 是否初始化。
	 */
	private boolean isInit=false;
	
	
	public TokenModel(){}
	
	/**
	 * 过期时间。
	 */
	private int exprieIn=7200;

	public Date getLastUpdTime() {
		return lastUpdTime;
	}

	public void setLastUpdTime(Date lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	public int getExprieIn() {
		return exprieIn;
	}

	public void setExprieIn(int exprieIn) {
		this.exprieIn = exprieIn;
	}
	
	/**
	 * 是否已经过期。
	 * @return
	 */
	public boolean isExpire(){
		long t=(new Date().getTime() -lastUpdTime.getTime())/1000;
		long time=this.exprieIn-t;
		if(time<60){
			return true;
		}
		return false;
	}
	
	/**
	 * 设置token。
	 * @param token
	 * @param expire
	 */
	public void setToken(String token,int expire){
		this.token=token;
		this.exprieIn=expire;
		this.isInit=true;
		this.lastUpdTime=new Date();
	}
	

}
