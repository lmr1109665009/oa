/**
 * 
 */
package com.suneee.ucp.base.service.system;

import java.io.IOException;
import java.util.*;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.event.def.ApolloMessage;
import com.suneee.ucp.base.event.def.MessageQuene;
import com.suneee.ucp.base.util.DateUtils;
import com.suneee.ucp.base.util.HttpUtils;

import net.sf.json.JSONObject;

/**
 * @author xiongxianyun
 *
 */
@Service
public class MessageService{
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
	
	/**
	 * 发送消息（与权限中心对接）
	 * @param jsonStr
	 */
	public void sendMessage(String jsonStr, String topic){
		// 异常文件全路径
		StringBuilder filePath = new StringBuilder();
		filePath.append(AppConfigUtil.get(Constants.MESSAGE_FAILED_FILEPATH))
			.append("message-failed-")
			.append(DateFormatUtil.format(new Date(), DateUtils.FORMAT_DATE_WITHOUT_LINE))
			.append(".txt");
		
		try {
			// 发送消息接口的json参数
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("topic", AppConfigUtil.get(Constants.MESSAGE_SENDMSG_TOPIC));
			reqParams.put("message", JSONObject.fromObject(jsonStr).toString().replaceAll("\"", "'"));
			JSONObject result = HttpUtils.sendToMessageQuene(Constants.MESSAGE_SENDMSG_API, JSONObject.fromObject(reqParams).toString());
			if(result == null || !"200".equals(result.get("status"))){
				LOGGER.error("消息发送到消息队列失败！消息数据：" + jsonStr);
				FileUtil.writeFile(filePath.toString(), jsonStr + Constants.SEPARATOR_LINE_BREAK, Constants.CHARSET_UTF8, true);
			}
		} catch (Exception e) {
			LOGGER.error("消息发送到消息队列失败！消息数据：" + jsonStr, e);
			FileUtil.writeFile(filePath.toString(), jsonStr + Constants.SEPARATOR_LINE_BREAK, Constants.CHARSET_UTF8, true);
		}
	}
	
	/**
	 * 发送消息（与权限中心对接）
	 * @param msgType 消息类型
	 * @param opType 操作类型
	 * @param msgData 消息数据
	 */
	public void sendMessage(int msgType, int opType, Object msgData){
		MessageQuene message = new MessageQuene();
		message.setEnterpriseCode(AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE));
		message.setType(msgType);
		message.setOpType(opType);
		message.setData(msgData);
		
		String jsonStr = JSONObject.fromObject(message).toString();
		this.sendMessage(jsonStr, AppConfigUtil.get(Constants.MESSAGE_SENDMSG_TOPIC));
	}
	
	/**
	 * 发送消息（与定子链对接）
	 * @param message
	 */
	public boolean sendMessage(ApolloMessage message){
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, String> datas = new HashMap<String, String>();
//		reqParams.put("topic", AppConfigUtil.get(Constants.MESSAGE_APOLLO_TOPIC));
//		reqParams.put("message", JSONObject.fromObject(message).toString());
//		reqParams.put("clientCode", AppConfigUtil.get(Constants.MESSAGE_APOLLO_CLIENTCODE));
		reqParams.put("enterpriseCode", message.getCompCode());
        reqParams.put("topic", message.getCompCode() +"_" + AppConfigUtil.get(Constants.MESSAGE_APOLLO_TOPIC));
        reqParams.put("system",(String)message.getTarget().get(1).get("system"));
		SysUser sysUser = (SysUser) message.getTarget().get(1).get("send");
		SysUser receiver = (SysUser) message.getTarget().get(1).get("receiver");
		reqParams.put("send", sysUser.getUserId().toString());
		reqParams.put("receiver", receiver.getUserId().toString());
        reqParams.put("receiveGroup", message.getCompCode() + "_gruop");

		List<Map<String, Object>> targets = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> target = message.getTarget();
		targets.add(target.get(0));
		message.setTarget(targets);
        reqParams.put("unique", random(12));
        if (1 == message.getMsgType()){
        	reqParams.put("operation","会议室消息");
		}else  if (2 == message.getMsgType()){
			reqParams.put("operation","请假");
		}
		else  if (3 == message.getMsgType()){
			reqParams.put("operation","外出");
		}
		else  if (4 == message.getMsgType()){
			reqParams.put("operation","出差");
		}
		else  if (5 == message.getMsgType()){
			reqParams.put("operation","审批");
		}
        reqParams.put("unique", random(12));

		datas.putAll(reqParams);
		datas.put("data", JSONObject.fromObject(message).toString());
		reqParams.put("msgData", JSONObject.fromObject(datas).toString());
		String params = JSONObject.fromObject(reqParams).toString();

		boolean isSuccess = true;
		try {
			String url = AppConfigUtil.get(Constants.MESSAGE_DZL_API_URL);
			JSONObject result = HttpUtils.sendToMessageQuene(url,Constants.MESSAGE_APOLLO_API, params);
			if(result == null || !"200".equals(result.get("status"))){
				LOGGER.error("发送定子链消息到消息队列失败！消息数据：" + params);
				isSuccess = false;
			}
		} catch (IOException e) {
			LOGGER.error("发送定子链消息到消息队列失败！消息数据：" + params, e);
			isSuccess = false;
		}
		return isSuccess;
	}

	public static String random(int num ){
        String randomcode = "";
        // 用字符数组的方式随机
        String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] m = model.toCharArray();
        for (int j = 0; j < num; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (randomcode.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            randomcode = randomcode + c;
        }
        return randomcode;
    }
}
