/**
 * @Title: ConferenceDeviceNotAvailableException.java 
 * @Package com.suneee.eas.oa.exception.conference 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.exception.conference;

/**
 * @ClassName: ConferenceDeviceNotAvailableException 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-02 14:17:34 
 *
 */
public class ConferenceDeviceNotAvailableException extends Exception{

	/**
	 * @Fields serialVersionUID:TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = 7753543755508706463L;

	public ConferenceDeviceNotAvailableException(String message, Throwable cause){
		super(message, cause);
	}
	
	public ConferenceDeviceNotAvailableException(String message){
		super(message);
	}
}
