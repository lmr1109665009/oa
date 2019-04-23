/**
 * @Title: NotParticipantException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: NotParticipantException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 11:45:45 
 *
 */
public class NotParticipantException extends Exception{
	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = -8030285528811551066L;

	public NotParticipantException(String message, Throwable cause){
		super(message, cause);
	}
	
	public NotParticipantException(String message){
		super(message);
	}
}
