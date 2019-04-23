package com.suneee.eas.oa.service.conference;

import java.util.List;

import com.suneee.eas.common.service.BaseService;
import com.suneee.eas.oa.model.conference.AuthorityInfo;

/**
 * @Description: AuthorityInfoService
 * @Author: kaize
 * @Date: 2018/8/2 16:34
 */
public interface AuthorityInfoService extends BaseService<AuthorityInfo> {
	
	/** 
	 * 保存授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerNames
	 * @param ownerType
	 */
	public void save(Long authId, String authType, String ownerIds, String ownerNames, String ownerType);
	
	/** 批量保存授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerNames
	 * @param ownerType
	 */
	public void batchSave(Long authId, String authType, List<Long> ownerIds, List<String> ownerNames, String ownerType);
	
	/** 更新授权信息
	 * @param authId
	 * @param authType
	 * @param ownerIds
	 * @param ownerNames
	 * @param ownerType
	 */
	public void update(Long authId, String authType, String ownerIds, String ownerNames, String ownerType);
	
	/** 删除授权信息
	 * @param authId
	 * @param authType
	 * @param ownerType
	 */
	public void deleteBy(Long authId, String authType, String ownerType);
	
	/** 获取授权信息
	 * @param authId
	 * @param authType
	 * @param ownerType
	 * @return
	 */
	public List<AuthorityInfo> getBy(Long authId, String authType, String ownerType);
}
