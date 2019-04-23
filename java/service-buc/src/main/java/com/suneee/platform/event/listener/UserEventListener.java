/**
 * 监听器，
 */
package com.suneee.platform.event.listener;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.cache.ICache;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.weixin.api.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.cache.ICache;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.weixin.api.IUserService;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.oa.service.user.UserSynclogService;
import com.suneee.ucp.base.event.def.MessageQuene;
import com.suneee.ucp.base.service.system.MessageService;
import com.suneee.ucp.base.service.system.SysUserExtService;

import net.sf.json.JSONObject;

@Service
@Async
public class UserEventListener implements ApplicationListener<UserEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
	@Resource 
	private SysUserService userService;
	@Resource
	private IUserService wxUserService;
	@Resource
	private SysUserExtService userExtService;
	@Resource 
	private MessageService msgService;
	@Resource
	private UserSynclogService userSynclogService;
	
	@Override
	public void onApplicationEvent(UserEvent ev) {
		boolean isSupportWx = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT,false);
		int action=ev.getAction();
		Long userId=ev.getUserId();
		SysUser user = ev.getUser();
		
		// 发送用户变更消息到消息队列
		this.sendMessage(ev);
		
		//用户添加事件
		if(action ==UserEvent.ACTION_ADD) {
			//do someThing
			
			//微信
			if(!isSupportWx) return;
			if(user.getHasSyncToWx()!= 0) wxUserService.update(user);
			else wxUserService.create(user);
		}else if(action ==UserEvent.ACTION_UPD){
			
			//微信
			if(!isSupportWx) return;
			if(user.getHasSyncToWx()!= 0) {
				if(user.getStatus() !=SysUser.STATUS_OK){
					wxUserService.delete(user.getAccount());
					//user.setHasSyncToWx(0);将状态恢复
				}else{
					wxUserService.update(user);
				}
			}
		}else{
			removeFromCache(userId);
			
			//微信
			if(!isSupportWx) return;
			user = userService.getById(userId);
			if(user.getHasSyncToWx()!= 0) 
			 wxUserService.delete(user.getAccount());
		}
	}

	private void removeFromCache(Long userId){
		
		ICache iCache=(ICache) AppUtil.getBean(ICache.class);
		
		String companyKey= ContextUtil.getCompanyKey(userId);
		
		String orgKey=ContextUtil.getOrgKey(userId);
		String positionKey=ContextUtil.getPositionKey(userId);
		
		//删除缓存数据
		iCache.delByKey(companyKey);
		iCache.delByKey(orgKey);
		iCache.delByKey(positionKey);
		
	}
	
	/**
	 * 发送用户变消息到消息队列
	 * @param user
	 * @param action
	 * @param enterpriseCodes
	 */
	private void sendMessage(UserEvent ev){
		Long userId = ev.getUserId();
		int action = ev.getAction();
		if(userId == null){
			LOGGER.error("同步用户信息失败：用户ID为空！");
			return;
		}
		
		// 将java对象转换为json对象
		SysUser user = ev.getUser();
		if(user == null){
			LOGGER.error("同步用户信息失败：ID为【" + userId + "】的用户不存在！");
			return;
		}
		
		JSONObject jsonObj = SysUser.toJsonObject(user);
		// 发送消息
		msgService.sendMessage(MessageQuene.TYPE_USER, action == UserEvent.ACTION_ADD ? UserEvent.ACTION_ADD : UserEvent.ACTION_UPD, 
				"[" + jsonObj.toString() + "]");
		
		// 不需要同步时，发完消息直接返回，不再调用用户中心接口
		if(!ev.isNeedSyncToUserCenter()){
			return;
		}
		Short opType = null;
		try {
			switch(action){
			// 新增用户
			case 0:
			// 修改用户信息
			case 1:
				if(user.getUcUserid() == null){
					opType = UserSynclog.OPTYPE_ADD_USER;
					userExtService.addToUserCenter(user);
				}else{
					opType = UserSynclog.OPTYPE_UPD_USER;
					userExtService.updateUserToUserCenter(user, false);
				}
				break;
			}
			// 用户信息同步成功，删除用户同步日志
			userSynclogService.deleteByUserId(user.getUserId());
		} catch (Exception e) {
			// 记录同步失败日志
			userSynclogService.save(user.getUserId(), opType, e.getMessage(), user.toString());
			LOGGER.error("同步用户信息失败：" + e.getMessage(), e);
		}
	}
}
