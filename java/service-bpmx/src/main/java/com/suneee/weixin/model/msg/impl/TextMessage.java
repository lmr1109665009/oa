package com.suneee.weixin.model.msg.impl;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.weixin.model.msg.BaseMessage;

/**
 * 文本消息对象。
 * <pre>
 * {
   "touser": "UserID1|UserID2|UserID3",
   "toparty": " PartyID1 | PartyID2 ",
   "totag": " TagID1 | TagID2 ",
   "msgtype": "text",
   "agentid": "1",
   "text": {
       "content": "Holiday Request For Pony(http://xxxxx)"
   },
   "safe":"0"
}</pre>
 * @author ray
 *
 */
public class TextMessage extends BaseMessage {
	
	public TextMessage(){}
	
	public TextMessage(String toUser,String toParty,String msg){
		super.setTouser(toUser);
		super.setToparty(toParty);
		this.setText(msg);
	}
	
	public String getMsgtype() {
		return "text";
	}
	
	private MsgContent text;
	
	public String getAgentid() {
		if(StringUtil.isEmpty(agentid)){
			this.agentid = PropertyUtil.getByAlias("wx.agentid");
		}
		return agentid;
	}
	
	public MsgContent getText() {
		return text;
	}

	public void setText(String text) {
		this.text = new MsgContent(text);
	}



	class MsgContent {
		private String content;

		public MsgContent(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}



	@Override
	public String toString() {
		String msgStr = JSONObject.toJSON(this).toString();
		return msgStr;
	}
	
	
	public static void main(String[] args) {
		TextMessage message=new TextMessage("zyg","","hello zyg");
		message.setAgentid("1");
		System.out.println(message);
	}
	

}
