/**
 * @Title: GlobalTypeServiceImpl.java 
 * @Package com.suneee.eas.oa.service.bpm.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.bpm.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.suneee.eas.common.constant.ServiceConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.api.bpmx.BpmApi;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.oa.service.bpm.GlobalTypeService;
import com.suneee.platform.model.system.GlobalType;

/**
 * @ClassName: GlobalTypeServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-21 14:40:02 
 *
 */
@Service
public class GlobalTypeServiceImpl implements GlobalTypeService{
	private static final Logger LOGGER = LogManager.getLogger(GlobalTypeServiceImpl.class);
	/** (non-Javadoc)
	 * @Title: getSceneTypeById 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param typeId
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.GlobalTypeService#getSceneTypeById(java.lang.Long)
	 */
	@Override
	public GlobalType getSceneTypeById(Long typeId) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("typeId",typeId);
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl() +BpmApi.getSceneTypeById + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getSceneTypeById, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data").toJavaObject(GlobalType.class);
		} else {
			throw new RuntimeException("Failed to obtain GlobalType information：" + result.getString("message"));
		}
	}

	/** 
	 * @Title: getByCatKey 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param catKey
	 * @param hasRoot
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @see com.suneee.eas.oa.service.bpm.GlobalTypeService#getSceneTypeList(java.lang.String, boolean)
	 */
	@Override
	public List<GlobalType> getSceneTypeList() throws UnsupportedEncodingException {
		LOGGER.info("Request Url: " + ServiceConstant.getBpmxServiceUrl()+BpmApi.getSceneTypeList + ", request parameter: null");
		JSONArray result = RestTemplateUtil.get(ServiceConstant.getBpmxServiceUrl()+BpmApi.getSceneTypeList, JSONArray.class, null);
		LOGGER.info("Response: " + result);
		List<GlobalType> list = new ArrayList<>();
		for(int i = 0; i < result.size(); i++){
			list.add(result.getJSONObject(i).toJavaObject(GlobalType.class));
		}
		return list;
	}
}
