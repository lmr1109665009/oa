/**
 * @Title: BpmFormHandlerService.java 
 * @Package com.suneee.eas.oa.service.bpm 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.bpm;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: BpmFormHandlerService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:54:28 
 *
 */
public interface BpmFormHandlerService {
	public String getBpmFormDataJson(Long runId) throws UnsupportedEncodingException;
}
