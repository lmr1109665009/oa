/**
 * @Title: ProcessRunService.java 
 * @Package com.suneee.eas.oa.service.bpm 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.bpm;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: ProcessRunService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:19:59 
 *
 */
public interface ProcessRunService {
	public JSONObject getProcessRun(Long runId) throws UnsupportedEncodingException;
	
	public Long saveForm(String actDefId, String formData, String flowKey) throws UnsupportedEncodingException;
}
