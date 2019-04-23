/**
 * @Title: AlreadyEndedException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: AlreadyEndedException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-10 12:00:36 
 *
 */
public class AlreadyEndedException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = 9016179861973863099L;

	public AlreadyEndedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public AlreadyEndedException(String message){
		super(message);
	}
}
