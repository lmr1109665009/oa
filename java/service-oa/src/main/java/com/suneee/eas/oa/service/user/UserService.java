/**
 * @Title: UserService.java 
 * @Package com.suneee.oa.service 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.user;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @ClassName: UserService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 14:23:35 
 *
 */
public interface UserService {
	/** 
	 * 批量获取用户信息
	 * @param userIdList
	 * @return
	 */
	List<SysUser> batchFindUser(List<Long> userIdList) throws UnsupportedEncodingException;
	
	/** 
	 * 获取用户详情
	 * @param userId
	 * @return
	 */
	public SysUser getUserDetails(Long userId) throws UnsupportedEncodingException;
	
	/** 获取用户所属组织列表
	 * @param userId
	 * @return
	 */
	public List<SysOrg> getOrgListByUserId(Long userId);

	/**
	 * @Description: 获取当前企业用户列表
	 * @Param:
	 * @return:
	 * @Author: liuhai
	 * @Date: 2018/8/21
	 */
	public List<SysUser> getCurrentEnterpriseUserList(MultiValueMap<String,Object> params) throws Exception;

	/**
	 * 批量获取组织信息
	 * @param orgIdList
	 * @return
	 */
	List<SysOrg> batchFindOrg(List<Long> orgIdList);
}
