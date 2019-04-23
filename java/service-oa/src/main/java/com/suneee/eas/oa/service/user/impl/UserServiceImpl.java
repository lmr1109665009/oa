/**
 * @Title: UserServiceImpl.java 
 * @Package com.suneee.eas.common.service.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.user.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.JsonUtil;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 14:20:27 
 *
 */
@Service
public class UserServiceImpl implements UserService{
	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

	/** (non-Javadoc)
	 * @Title: batchFindUser 
	 * @Description: 批量获取用户信息
	 * @param userIdList
	 * @return
	 * @see com.suneee.eas.oa.service.user.UserService#batchFindUser(java.util.List)
	 */
	@Override
	public List<SysUser> batchFindUser(List<Long> userIdList) throws UnsupportedEncodingException {
		Map<String,Object> params=new HashMap<>();
		params.put("ids",StringUtils.join(userIdList,","));
		LOGGER.info("Request Url: " + ServiceConstant.getBucServiceUrl() +UserApi.batchFindUser + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.post(ServiceConstant.getBucServiceUrl()+UserApi.batchFindUser, JSONObject.class, JsonUtil.toJson(params));
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			JSONArray array = result.getJSONArray("data");
			return array.toJavaList(SysUser.class);
		} else {
			throw new RuntimeException("Failed to obtain the org list of user belong to : " + result.getString("message"));
		}
	}
	
	/** 
	 * 获取用户详情
	 * @param userId
	 * @return
	 */
	public SysUser getUserDetails(Long userId) throws UnsupportedEncodingException {
		MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
		params.add("userId",userId);
		LOGGER.info("Request Url: " + ServiceConstant.getBucServiceUrl()+UserApi.getUserDetails + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBucServiceUrl()+UserApi.getUserDetails, JSONObject.class, params);
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			return result.getJSONObject("data").getJSONObject("user").toJavaObject(SysUser.class);
		} else {
			throw new RuntimeException("Failed to obtain user information：" + result.getString("message"));
		}
	}
	
	/** 
	 * @Title: getOrgListByUserId 
	 * @Description: 获取用户所属组织列表
	 * @param userId
	 * @return 
	 * @see com.suneee.eas.oa.service.user.UserService#getOrgListByUserId(java.lang.Long)
	 */
	public List<SysOrg> getOrgListByUserId(Long userId){
		Map<String,Object> params=new HashMap<>();
		params.put("userId", userId);
		params.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		LOGGER.info("Request Url: " + ServiceConstant.getBucServiceUrl()+UserApi.getOrgListByUserId + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.post(ServiceConstant.getBucServiceUrl()+UserApi.getOrgListByUserId, JSONObject.class, JsonUtil.toJson(params));
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			JSONArray array = result.getJSONArray("data");
			return array.toJavaList(SysOrg.class);
		} else {
			throw new RuntimeException("Failed to obtain user information：" + result.getString("message"));
		}
	}

	/**
	 * 获取当前企业用户列表
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<SysUser> getCurrentEnterpriseUserList(MultiValueMap<String,Object> params) throws Exception{
		JSONObject result = RestTemplateUtil.get(ServiceConstant.getBucServiceUrl()+UserApi.getCurrentEnterpriseUserList, JSONObject.class, params);
		if(null == result){
			throw new RuntimeException("Failed to obtain user information： resutl null");
		}
		if(0 == result.getIntValue("status")){
			JSONArray array = result.getJSONArray("data");
			return array.toJavaList(SysUser.class);
		}else{
			throw new RuntimeException("Failed to obtain user information：" + result.getString("message"));
		}
	}

	/**
	 * 批量获取组织信息
	 * @param orgIdList
	 * @return
	 */
	@Override
	public List<SysOrg> batchFindOrg(List<Long> orgIdList){
		Map<String,Object> params=new HashMap<>();
		params.put("ids",StringUtils.join(orgIdList,","));
		LOGGER.info("Request Url: " + ServiceConstant.getBucServiceUrl()+UserApi.batchFindOrg + ", request parameter: " + params);
		JSONObject result = RestTemplateUtil.post(ServiceConstant.getBucServiceUrl()+UserApi.batchFindOrg, JSONObject.class, JsonUtil.toJson(params));
		LOGGER.info("Response: " + result);
		if(result.getIntValue("status") == 0){
			JSONArray array = result.getJSONArray("data");
			return array.toJavaList(SysOrg.class);
		} else {
			throw new RuntimeException("Failed to obtain the org information : " + result.getString("message"));
		}
	}
}
