/**
 * 
 */
package com.suneee.ucp.base.event.def;

/**
 * 发送消息的内容对象
 * @author Administrator
 *
 */
public class MessageQuene {
	
	/** 消息类型：1-用户消息 **/
	public static final int TYPE_USER = 1;
	/** 消息类型：2-组织消息 **/
	public static final int TYPE_ORG = 2;
	/** 消息类型：3-用户组织关系消息 */
	public static final int TYPE_USERORG = 3;
	/** 消息类型：4-岗位消息 */
	public static final int TYPE_POS = 4;
	
	/** 操作类型：0-新增 **/
	public static final int OPTYPE_ADD = 0;
	/** 操作类型：1-更新 **/
	public static final int OPTYPE_UPD = 1;
	/** 操作类型：2-删除 **/
	public static final int OPTYPE_DEL = 2;
	/**
	 * 企业编码
	 */
	private String enterpriseCode;
	
	/**
	 * 消息类型：1-用户消息，2-组织消息，3-用户组织关系消息，4-岗位消息
	 */
	private int type;
	
	/**
	 * 操作类型：0-新增，1-更新，2-删除
	 */
	private int opType;
	
	/**
	 * 数据
	 */
	private Object data;

	/**
	 * @return the enterpriseCode
	 */
	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	/**
	 * @param enterpriseCode the enterpriseCode to set
	 */
	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the opType
	 */
	public int getOpType() {
		return opType;
	}

	/**
	 * @param opType the opType to set
	 */
	public void setOpType(int opType) {
		this.opType = opType;
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
}
