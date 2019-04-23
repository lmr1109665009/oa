/**
 * 
 */
package com.suneee.ucp.base.event.listener;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.suneee.core.util.DateFormatUtil;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.oa.service.user.UserSynclogService;
import com.suneee.ucp.base.event.def.MessageQuene;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import com.suneee.ucp.base.service.system.MessageService;
import com.suneee.ucp.base.service.system.SysUserExtService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 *
 */
@Service
@Async
public class UserPositionEventListener implements ApplicationListener<UserPositionEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserPositionEventListener.class);
	@Resource 
	private MessageService msgService;
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private UserSynclogService userSynclogService;
	
	@Override
	public void onApplicationEvent(UserPositionEvent userPosEvent) {
		// 获取用户岗位（组织）关系信息对象
		List<UserPosition> userPositionList = userPosEvent.getUserPositionList();
		if(userPositionList == null || userPositionList.isEmpty()){
			LOGGER.debug("用户岗位关系信息对象为空，不发送同步消息。");
			return;
		}
		
		// 将java集合转换为json数组
		JSONArray jsonArr = JSONArray.fromObject(userPositionList);
		JSONObject jsonObj = null;
		Date createTime = null;
		Date updateTime = null;
		for(int i = 0; i < jsonArr.size(); i++){
			jsonObj = jsonArr.getJSONObject(i);
			createTime = userPositionList.get(i).getCreatetime();
			jsonObj.put("createtime", createTime == null ? null : DateFormatUtil.formaDatetTime(createTime));
			updateTime = userPositionList.get(i).getUpdatetime();
			jsonObj.put("updatetime", updateTime == null ? null : DateFormatUtil.formaDatetTime(updateTime));
		}
		
		// 发送消息
		int action = userPosEvent.getAction();
		if(action == UserPositionEvent.ACTION_DEL || action == UserPositionEvent.ACTION_DEL_NC){
			action = UserPositionEvent.ACTION_UPD;
		}
		msgService.sendMessage(MessageQuene.TYPE_USERORG, action, jsonArr);
	}
}
