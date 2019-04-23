/**
 * @Title: UserEnterpriseEventListener.java 
 * @Package com.suneee.ucp.base.event.listener 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.ucp.base.event.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.suneee.platform.model.system.SysUser;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.oa.service.user.UserSynclogService;
import com.suneee.ucp.base.event.def.UserEnterpriseEvent;
import com.suneee.ucp.base.service.system.SysUserExtService;

/**
 * @ClassName: UserEnterpriseEventListener 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-08 09:44:07 
 *
 */
@Service
@Async
public class UserEnterpriseEventListener implements ApplicationListener<UserEnterpriseEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEnterpriseEventListener.class);
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private UserSynclogService userSynclogService;
	
	/** (non-Javadoc)
	 * @Title: onApplicationEvent 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param event 
	 * @see ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(UserEnterpriseEvent event) {
		// 获取用户企业关系列表
		List<UserEnterprise> userEnterpriseList = event.getUserEnterpriseList();
		SysUser sysUser = event.getSysUser();
		if(userEnterpriseList == null || userEnterpriseList.isEmpty() || sysUser == null){
			return;
		}
		int action= event.getAction();
		Short opType = UserSynclog.OPTYPE_UPD_USER_ORG;
		try {
			// 更新用户企业关系
			if(action == UserEnterpriseEvent.ACTION_ADD){
				this.updateUserEnterprise(sysUser, userEnterpriseList);
			} 
			// 删除用户企业关系
			else if(action == UserEnterpriseEvent.ACTION_DEL){
				opType = UserSynclog.OPTYPE_DEL_USER_ORG;
				this.delUserEnterprise(sysUser, userEnterpriseList);
			}
		} catch (Exception e) {
			userSynclogService.save(sysUser.getUserId(), opType, e.getMessage(), sysUser.toString());
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/** 
	 * 更新用户企业关系
	 * @param user 用户信息对象
	 * @param userEnterpriseList 用户企业关系集合
	 */
	private final void updateUserEnterprise(SysUser user, List<UserEnterprise> userEnterpriseList) throws Exception{
		// 企业编码：集团编码键值对
		Map<String, String> enterpriseCodes = new HashMap<String, String>();
		for(UserEnterprise userEnterprise : userEnterpriseList){
			enterpriseCodes.put(userEnterprise.getEnterpriseCode(), userEnterprise.getGroupCode());
		}
		// 更新用户企业关系
		sysUserExtService.updateToUserCenter(user, enterpriseCodes);
		// 更新用户企业关系成功，则删除用户信息同步日志
		userSynclogService.deleteByUserId(user.getUserId());
	}
	
	/** 
	 * 删除用户企业关系
	 * @param user 用户信息对象
	 * @param userEnterpriseList 用户企业关系集合
	 */
	private final void delUserEnterprise(SysUser user, List<UserEnterprise> userEnterpriseList)throws Exception{
		// 企业编码集合
		Set<String> enterpriseCodeSet =new HashSet<String>();
		for(UserEnterprise userEnterprise : userEnterpriseList){
			enterpriseCodeSet.add(userEnterprise.getEnterpriseCode());
		}
		
		// 删除用户企业关系
		sysUserExtService.delUserOrgFromUserCenter(user, enterpriseCodeSet);
		// 删除用户企业关系成功，则删除用户信息同步日志
		userSynclogService.deleteByUserId(user.getUserId());
	}

}
