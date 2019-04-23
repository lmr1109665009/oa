/**
 * @Title: IllegalTargetTypeException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: IllegalTargetTypeException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-06 15:05:10 
 *
 */
public class IllegalTargetTypeException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -5831676003095455897L;
	public IllegalTargetTypeException(String message, Throwable cause){
		super(message, cause);
	}
	public IllegalTargetTypeException(String message){
		super(message);
	}
}
