/**
 * @Title: ConferenceTimeConflictException.java 
 * @Package com.suneee.eas.oa 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: ConferenceTimeConflictException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-02 10:38:05 
 *
 */
public class ConferenceTimeConflictException extends Exception{

	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -552002030424128557L;

	public ConferenceTimeConflictException(String message, Throwable cause){
		super(message, cause);
	}
	
	public ConferenceTimeConflictException(String message){
		super(message);
	}
}
