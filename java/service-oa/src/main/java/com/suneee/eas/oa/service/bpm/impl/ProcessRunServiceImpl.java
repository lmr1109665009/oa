/**
 * @Title: ProcessRunServiceImpl.java 
 * @Package com.suneee.eas.oa.service.bpm.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.bpm.impl;

import java.io.UnsupportedEncodingException;

import com.suneee.eas.common.constant.ServiceConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.api.bpmx.BpmApi;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.oa.service.bpm.ProcessRunService;

/**
 * @ClassName: ProcessRunServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:20:16 
 *
 */
@Service
public class ProcessRunServiceImpl implements ProcessRunService{
	private static final Logger LOGGER = LogManager.getLogger(ProcessRunServiceImpl.class);
	/** 
	 * @Title: getProcessRun 
	 * @Description: 获取流程实例
	 * @param runId
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.ProcessRunService#getProcessRun(java.lang.Long)
	 */
	@Override
	public JSONObject getProcessRun(Long runId) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("runId",runId);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl() +BpmApi.getProcessRun + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getProcessRun, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data");
		} else {
			throw new RuntimeException("Failed to obtain ProcessRun information：" + result.getString("message"));
		}
	}
	
	/** 
	 * @Title: saveForm 
	 * @Description: 保存草稿
	 * @param actDefId
	 * @param formData
	 * @param flowKey
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.ProcessRunService#saveForm(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public Long saveForm(String actDefId, String formData, String flowKey) throws UnsupportedEncodingException{
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("actDefId", actDefId);
		params.add("formData", formData);
		params.add("flowKey", flowKey);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl()+BpmApi.saveForm + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.post(ServiceConstant.getBpmxServiceUrl()+BpmApi.saveForm, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getLong("data");
		} else {
			throw new RuntimeException("Failed to obtain ProcessRun information：" + result.getString("message"));
		}
	}

}
