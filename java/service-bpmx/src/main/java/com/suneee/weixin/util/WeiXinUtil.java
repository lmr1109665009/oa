package com.suneee.weixin.util;

import com.suneee.core.util.HttpUtil;
import com.suneee.weixin.api.WeixinConsts;
import com.suneee.weixin.model.msg.impl.NewsMessage;
import com.suneee.weixin.model.msg.impl.TextMessage;

/**
 * 微信工具类。
 * @author ray
 *
 */
public class WeiXinUtil {
	
	/**
	 * 发送文本消息给企业成员。
	 * @param msg
	 * @return
	 */
	public static String sendTextMessage(TextMessage msg){
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getSendMsgUrl(),msg.toString(),WeixinConsts.METHOD_POST);
		return resultJson;
	}
	
	/**
	 * 发送新闻消息给指定用户。
	 * @param msg
	 * @return
	 */
	public static String sendNewsMessage(NewsMessage msg){
		String resultJson = HttpUtil.sendHttpsRequest(WeixinConsts.getSendMsgUrl(),msg.toString(),WeixinConsts.METHOD_POST);
		return resultJson;
	}
}
