/**
 * @Title: BpmFormHandlerServiceImpl.java 
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
import com.suneee.eas.oa.service.bpm.BpmFormHandlerService;

/**
 * @ClassName: BpmFormHandlerServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:54:37 
 *
 */
@Service
public class BpmFormHandlerServiceImpl implements BpmFormHandlerService{
	private static final Logger LOGGER = LogManager.getLogger(BpmFormHandlerServiceImpl.class);
	/** (non-Javadoc)
	 * @Title: getBpmFormDataJson 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param runId
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.BpmFormHandlerService#getBpmFormDataJson(com.alibaba.fastjson.JSONObject, java.lang.String, java.lang.String)
	 */
	@Override
	public String getBpmFormDataJson(Long runId) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("runId",runId);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl() +BpmApi.getBpmTableJson + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getBpmTableJson, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data").toJSONString();
		} else {
			throw new RuntimeException("Failed to obtain BpmFormData json String ：" + result.getString("message"));
		}
	}

}
