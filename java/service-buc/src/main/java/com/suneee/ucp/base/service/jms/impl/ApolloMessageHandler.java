/**
 * 
 */
package com.suneee.ucp.base.service.jms.impl;

import com.suneee.core.jms.IMessageHandler;
import com.suneee.core.model.MessageModel;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.util.MessageUtil;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.event.def.ApolloMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiongxianyun
 *
 */
public class ApolloMessageHandler implements IMessageHandler{
	@Resource
	private GlobalTypeService globalTypeService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ApolloMessageHandler.class);
	@Override
	public String getTitle() {
		return "定子链消息";
	}

	@Override
	public boolean getIsDefaultChecked() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handMessage(MessageModel model) {
		if(model.getReceiveUser() == null){
			LOGGER.info("未发送定子链审批消息：没有消息接收人");
			return;
		}
		// 消息接收人
		SysUser sysUser = (SysUser)model.getReceiveUser();
		if(sysUser == null){
			LOGGER.warn("定子链审批消息发送失败：消息接收人不存在！");
			return;
		}
		// 消息内容
		String content = MessageUtil.getContent(model,false,true);
		String url = "";
		if (BeanUtils.isNotIncZeroEmpty(model.getExtId())) {
			url = ServiceUtil.getUrl(model.getExtId().toString(), model.getIsTask());
			url = url + "&flag=web";
		}
		content = content + "：" + url;
		
		Map<String, Object> vars = model.getVars();
		/*
		// 消息的业务数据
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("creator", vars.get("creator"));
		jsonObj.accumulate("subject", model.getSubject());
		jsonObj.accumulate("processName", vars.get("processName"));*/
		// 获取流程对应的企业编码
		GlobalType globalType=globalTypeService.getById(Long.valueOf(vars.get("typeId").toString()));
		if(globalType == null){
			LOGGER.warn("定子链审批消息发送失败：流程【" +  vars.get("processName") + "】没有所属企业编码！");
			return;
		}
		
		List<Map<String, Object>> targets = new ArrayList<Map<String, Object>>();
		Map<String, Object> receiver = new HashMap<String, Object>();
		receiver.put("email", sysUser.getEmail());
		receiver.put("enterpriseCode", globalType);
		targets.add(receiver);
		EventUtil.publishApolloMessageEvent(ApolloMessage.MSG_TYPE_APPROVAL, targets, 
				ApolloMessage.TARGET_TYPE_PRIVATE, Integer.valueOf(vars.get("status").toString()), content);

	}
}
