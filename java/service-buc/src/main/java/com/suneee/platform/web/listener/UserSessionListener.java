package com.suneee.platform.web.listener;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.cache.ICache;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 监听用户登录事件和会话过期事件。
 * 管理在线用户情况。
 * @author csx
 *
 */
@WebListener
public class UserSessionListener implements HttpSessionAttributeListener{

	protected Logger logger = LoggerFactory.getLogger(UserSessionListener.class);
	
	private static String ONLINE_USERS="onLineUsers_";
	/**
	 * 获取在线用户
	 * @return
	 */
	public static Map<Long ,OnlineUser> getOnLineUsers(){
		ICache icache= AppUtil.getBean(ICache.class);
		Map<Long ,OnlineUser> usersMap= (Map<Long, OnlineUser>) icache.getByKey(ONLINE_USERS);
		if(BeanUtils.isEmpty(usersMap)){
			usersMap=new HashMap<Long ,OnlineUser>();
		}
		return usersMap;
	}
	
	/**
	 * 添加在线用户。
	 * @param onlineUser
	 */
	public static void addOnlineUser(OnlineUser onlineUser){
		ICache icache=AppUtil.getBean(ICache.class);
		Map<Long ,OnlineUser> usersMap= (Map<Long, OnlineUser>) icache.getByKey(ONLINE_USERS);
		if(BeanUtils.isEmpty(usersMap)){
			usersMap=new HashMap<Long ,OnlineUser>();
		}
		usersMap.put(onlineUser.getUserId(), onlineUser);
		icache.add(ONLINE_USERS, usersMap);
	}
	
	/**
	 * 删除在线用户。
	 * @param userId
	 */
	public static void removeUser(Long userId){
		ICache icache=AppUtil.getBean(ICache.class);
		Map<Long ,OnlineUser> usersMap= (Map<Long, OnlineUser>) icache.getByKey(ONLINE_USERS);
		if(BeanUtils.isNotEmpty(usersMap)){
			usersMap.remove(userId);
		}
		else{
			usersMap=new HashMap<Long ,OnlineUser>();
		}
		icache.add(ONLINE_USERS, usersMap);
	}
	
	
	/**
	 * 进入系统,添加在线用户
	 */
	public void attributeAdded(HttpSessionBindingEvent event) {
		if(!"SPRING_SECURITY_LAST_USERNAME".equals(event.getName())) return;
		SysUser user=(SysUser) ContextUtil.getCurrentUser();
		if(user==null){
			return;
		}
		
		OnlineUser onlineUser=new OnlineUser();
		
		onlineUser.setUserId(user.getUserId());
		onlineUser.setUsername(user.getUsername());
		SysOrg org=(SysOrg) ContextUtil.getCurrentOrg();
		if(org!=null){
			onlineUser.setOrgId(org.getOrgId());
			onlineUser.setOrgName(org.getOrgName());
		}
		
		addOnlineUser(onlineUser);
	}

	/**
	 * 退出系统，或者系统超时时+-删除在线用户
	 */
	public void attributeRemoved(HttpSessionBindingEvent event) {
		if("SPRING_SECURITY_LAST_USERNAME".equals(event.getName())){
			SysUser	user = AppUtil.getBean(SysUserService.class).getByAccount((String)event.getValue());
			if(user!=null){
				removeUser(user.getUserId());
			}
		
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		logger.info(event.getName());
		
	}

	


}
