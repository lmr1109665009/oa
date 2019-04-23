/**
 * @Title: BpmDefinitionServiceImpl.java 
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
import com.suneee.eas.oa.service.bpm.BpmDefinitionService;

/**
 * @ClassName: BpmDefinitionServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:19:21 
 *
 */
@Service
public class BpmDefinitionServiceImpl implements BpmDefinitionService{
	private static final Logger LOGGER = LogManager.getLogger(BpmDefinitionServiceImpl.class);
	/** (non-Javadoc)
	 * @Title: getByDefKey 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param defKey
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.BpmDefinitionService#getByDefKey(java.lang.String)
	 */
	@Override
	public JSONObject getByDefKey(String defKey) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("defKey",defKey);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl()+BpmApi.getDefByKey+ ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getDefByKey, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data");
		} else {
			throw new RuntimeException("Failed to obtain BpmDefinition information：" + result.getString("message"));
		}
	}

	/** (non-Javadoc)
	 * @Title: getSceneDefByDefId 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param defId
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.BpmDefinitionService#getSceneDefByDefId(java.lang.Long)
	 */
	@Override
	public JSONObject getSceneDefByDefId(Long defId) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("defId",defId);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl() +BpmApi.getSceneDefByDefId + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getSceneDefByDefId, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data");
		} else {
			throw new RuntimeException("Failed to obtain BpmDefinition information：" + result.getString("message"));
		}
	}

}
