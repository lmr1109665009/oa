/**
 * @Title: IllegalAuditStatusException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: IllegalAuditStatusException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-06 14:52:23 
 *
 */
public class IllegalStatusException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -4593318926841740670L;
	
	public IllegalStatusException(String message, Throwable cause){
		super(message, cause);
	}
	public IllegalStatusException(String message){
		super(message);
	}
}
