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
import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.event.def.MessageQuene;
import com.suneee.ucp.base.event.def.OrgEvent;
import com.suneee.ucp.base.service.system.MessageService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 组织事件监听器
 * 
 * @author xiongxianyun
 *
 */
@Service
@Async
public class OrgEventListener implements ApplicationListener<OrgEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(OrgEventListener.class);
	@Resource 
	private MessageService msgService;
	
	@Override
	public void onApplicationEvent(OrgEvent orgEvent) {
		// 获取操作的组织信息对象
		List<SysOrg> sysOrgList = orgEvent.getSysOrgList();
		if(sysOrgList == null || sysOrgList.isEmpty()){
			LOGGER.debug("组织信息对象为空，不发送同步消息。");
			return;
		}
		// 将java集合转换为json数组
		JSONArray jsonArr = JSONArray.fromObject(sysOrgList);
		JSONObject jsonObj = null;
		Date createTime = null;
		Date updateTime = null;
		for(int i = 0; i< jsonArr.size(); i++){
			jsonObj = jsonArr.getJSONObject(i);
			createTime = sysOrgList.get(i).getCreatetime();
			jsonObj.put("createtime", createTime == null ? null : DateFormatUtil.formaDatetTime(createTime));
			updateTime =  sysOrgList.get(i).getUpdatetime();
			jsonObj.put("updatetime", updateTime == null ? null : DateFormatUtil.formaDatetTime(updateTime));
			
		}
		
		// 发送消息
		msgService.sendMessage(MessageQuene.TYPE_ORG, orgEvent.getAction(), jsonArr);
	}
}
