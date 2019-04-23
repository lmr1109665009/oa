/**
 * 
 */
package com.suneee.ucp.base.event.listener;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.suneee.core.util.DateFormatUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.ucp.base.event.def.MessageQuene;
import com.suneee.ucp.base.event.def.PositionEvent;
import com.suneee.ucp.base.service.system.MessageService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 *
 */
@Async
@Service
public class PositionEventListener implements ApplicationListener<PositionEvent>{
	private static final Logger LOGGER = LoggerFactory.getLogger(PositionEventListener.class);
	@Resource 
	private MessageService msgService;
	
	@Override
	public void onApplicationEvent(PositionEvent posEvent) {
		// 获取岗位信息对象列表
		List<Position> posList = posEvent.getPositionList();
		if(posList == null || posList.isEmpty()){
			LOGGER.debug("岗位信息对象为空，不发送同步消息。");
			return;
		}
		
		// 将java对象集合转换为json数组
		//JSONArray jsonArr = JSONArray.fromObject(posList);
		JSONArray jsonArr = new JSONArray();
		//处理时间格式
		for (Position p:posList){
			JSONObject jsonObject = JSONObject.fromObject(p);
			jsonObject.put("createtime", p.getCreatetime() == null ? null : DateFormatUtil.formaDatetTime(p.getCreatetime()));
			jsonObject.put("updatetime", p.getUpdatetime() == null ? null : DateFormatUtil.formaDatetTime(p.getUpdatetime()));
			jsonArr.add(jsonObject);
		}
		// 发送岗位消息
		msgService.sendMessage(MessageQuene.TYPE_POS, posEvent.getAction(), jsonArr);
	}

}
