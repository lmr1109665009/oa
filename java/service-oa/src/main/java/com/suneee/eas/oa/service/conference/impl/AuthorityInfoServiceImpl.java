package com.suneee.eas.oa.service.conference.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.oa.dao.conference.AuthorityInfoDao;
import com.suneee.eas.oa.model.conference.AuthorityInfo;
import com.suneee.eas.oa.service.conference.AuthorityInfoService;

/**
 * @Description:
 * @Author: kaize
 * @Date: 2018/8/2 16:36
 */
@Service
public class AuthorityInfoServiceImpl extends BaseServiceImpl<AuthorityInfo> implements AuthorityInfoService {
	private static final Logger LOGGER = LogManager.getLogger(AuthorityInfoServiceImpl.class);
	private AuthorityInfoDao authorityInfoDao;

	@Autowired
	public void setAuthorityInfoDao(AuthorityInfoDao authorityInfoDao) {
		this.authorityInfoDao = authorityInfoDao;
		setBaseDao(authorityInfoDao);
	}
	
	/**  
	 * @Title: save 
	 * @Description: 保存授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerType 
	 * @see com.suneee.eas.oa.service.conference.AuthorityInfoService#save(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void save(Long authId, String authType, String ownerIds, String ownerNames, String ownerType){
		if(StringUtils.isBlank(ownerIds) || StringUtils.isBlank(ownerNames)){
			LOGGER.info("ownerIds or ownerNames is blank, do not perform a save operation.");
			return;
		}
		String[] ownerIdArr = ownerIds.split(",");
		List<Long> ownerIdList = new ArrayList<Long>();
		for(String ownerId : ownerIdArr){
			ownerIdList.add(Long.parseLong(ownerId));
		}
		String[] ownerNameArr = ownerNames.split(",");
		List<String> ownerNameList = Arrays.asList(ownerNameArr);
		this.batchSave(authId, authType, ownerIdList, ownerNameList, ownerType);
	}

	/**  
	 * @Title: batchSave 
	 * @Description: 批量保存授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerType 
	 * @see com.suneee.eas.oa.service.conference.AuthorityInfoService#batchSave(java.lang.Long, java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public void batchSave(Long authId, String authType, List<Long> ownerIds, List<String> ownerNames, String ownerType) {
		if(authId == null || authType == null || ownerIds == null || ownerNames == null || ownerType == null){
			throw new IllegalArgumentException("authId, authType, ownerIds and ownerType must not be null.");
		}
		
		if(ownerIds.size() != ownerNames.size()){
			throw new IllegalArgumentException("the length of 'ownerIds' is not accordance with 'ownerNames'.");
		}
		// 构造授权信息
		List<AuthorityInfo> authlist = new ArrayList<AuthorityInfo>();
		AuthorityInfo authorityInfo = null;
		for(int i = 0; i < ownerIds.size(); i++){
			authorityInfo = new AuthorityInfo();
			authorityInfo.setAuthId(authId);
			authorityInfo.setAuthType(authType);
			authorityInfo.setOwnerId(ownerIds.get(i));
			authorityInfo.setOwnerType(ownerType);
			authorityInfo.setOwnerName(ownerNames.get(i));
			authlist.add(authorityInfo);
		}
		
		// 批量保存
		authorityInfoDao.batchSave(authlist);
	}
	
	/** 
	 * @Title: update 
	 * @Description: 更新授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerNames
	 * @param ownerType 
	 * @see com.suneee.eas.oa.service.conference.AuthorityInfoService#update(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void update(Long authId, String authType, String ownerIds, String ownerNames, String ownerType){
		// 删除已经存在的权限信息
		this.deleteBy(authId, authType, ownerType);
		// 添加新的权限信息
		this.save(authId, authType, ownerIds, ownerNames, ownerType);
	}
	
	/** 
	 * @Title: deleteBy 
	 * @Description: 删除授权信息
	 * @param authId
	 * @param authType
	 * @param ownerType 
	 * @see com.suneee.eas.oa.service.conference.AuthorityInfoService#deleteBy(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBy(Long authId, String authType, String ownerType){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("authId", authId);
		params.put("authType", authType);
		params.put("ownerType", ownerType);
		authorityInfoDao.deleteBy(params);
	}
	
	/** 
	 * @Title: getBy 
	 * @Description: 获取授权信息
	 * @param authId
	 * @param authType
	 * @param ownerType
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.AuthorityInfoService#getBy(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public List<AuthorityInfo> getBy(Long authId, String authType, String ownerType){
		QueryFilter filter = new QueryFilter();
		filter.setSqlKey("listAll");
		filter.addFilter("authId", authId);
		filter.addFilter("authType", authType);
		filter.addFilter("ownerType", ownerType);
		return authorityInfoDao.getListBySqlKey(filter);
	}
}
