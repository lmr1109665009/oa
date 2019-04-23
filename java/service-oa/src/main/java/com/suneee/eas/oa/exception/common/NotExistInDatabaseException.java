/**
 * @Title: NotExitInDatabaseException.java 
 * @Package com.suneee.eas.oa.exception.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.common;

/**
 * @ClassName: NotExitInDatabaseException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-03 18:03:37 
 *
 */
public class NotExistInDatabaseException extends RuntimeException{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = 8083114371443806077L;

	public NotExistInDatabaseException(String message, Throwable cause){
		super(message, cause);
	}
	
	public NotExistInDatabaseException(String message){
		super(message);
	}
}
