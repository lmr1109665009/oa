/**
 * @Title: AlreadyExistException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: AlreadyExistException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-10 15:34:45 
 *
 */
public class AlreadyExistException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -8141281946675688053L;
	public AlreadyExistException(String message, Throwable cause){
		super(message, cause);
	}
	public AlreadyExistException(String message){
		super(message);
	}
}
