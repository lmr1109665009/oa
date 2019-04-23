/**
 * @Title: AlreadyStartedException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: AlreadyStartedException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 18:08:30 
 *
 */
public class AlreadyStartedException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -2088741344629492656L;

	public AlreadyStartedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public AlreadyStartedException(String message){
		super(message);
	}
}
