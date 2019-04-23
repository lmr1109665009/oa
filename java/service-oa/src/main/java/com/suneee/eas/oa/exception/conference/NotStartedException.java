/**
 * @Title: NotStartedException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: NotStartedException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-10 13:23:47 
 *
 */
public class NotStartedException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = 3663327399855742714L;
	public NotStartedException(String message, Throwable cause){
		super(message, cause);
	}
	public NotStartedException(String message){
		super(message);
	}
}
