/**
 * 
 */
package com.suneee.ucp.base.event.def;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class ApolloMessage {
	
	/**
	 * 消息类型：会议室
	 */
	public static final int MSG_TYPE_CONFERENCE = 1;
	/**
	 * 消息类型：请假
	 */
	public static final int MSG_TYPE_LEAVE = 2;
	/**
	 * 消息类型：外出
	 */
	public static final int MSG_TYPE_OUT = 3;
	/**
	 * 消息类型：出差
	 */
	public static final int MSG_TYPE_BUSINESS = 4;
	/**
	 * 消息类型：审批
	 */
	public static final int MSG_TYPE_APPROVAL = 5;
	
	/**
	 * 目标对象类型：频道
	 */
	public static final int TARGET_TYPE_PUBLIC = 1;
	/**
	 * 目标对象类型：个人
	 */
	public static final int TARGET_TYPE_PRIVATE = 2;
	
	/**
	 * 会议事消息状态：预订
	 */
	public static final int STATUS_CONFERENCE_ADD = 1;
	
	/**
	 * 会议室消息状态：取消
	 */
	public static final int STATUS_CONFERENCE_DEL = 2;

	/**
	 * 消息类型
	 */
	private int msgType;
	
	/**
	 * 目标对象
	 */
	private List<Map<String, Object>> target;
	
	/**
	 * 目标对象类型
	 */
	private int targetType;
	
	/**
	 * 状态
	 */
	private int status;
	
	/**
	 * 业务数据对象
	 */
	private Object data;
	
	/**
	 * 企业编码
	 */ 
	private String compCode;

	/**
	 * @return the msgType
	 */
	public int getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	/**
	 * @return the target
	 */
	public List<Map<String, Object>> getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(List<Map<String, Object>> target) {
		this.target = target;
	}

	/**
	 * @return the targetType
	 */
	public int getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the compCode
	 */
	public String getCompCode() {
		return compCode;
	}

	/**
	 * @param compCode the compCode to set
	 */
	public void setCompCode(String compCode) {
		this.compCode = compCode;
	}
}
