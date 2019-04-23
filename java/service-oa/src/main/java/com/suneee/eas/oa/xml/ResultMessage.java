package com.suneee.eas.oa.xml;

import net.sf.json.util.JSONStringer;

import java.io.Serializable;

/**
 * 消息处理
 * 
 * @author hotent
 * 
 */
public class ResultMessage implements Serializable{
	/** */
	private static final long serialVersionUID = -7102370526170507252L;

	/** 成功 */
	public static final int Success = 1;

	/** 失败 */
	public static final int Fail = 0;

	
	// 返回结果(成功或失败)
	private int result = Success;
	// 返回消息
	private String message = "";
	// 引起原因
	private String cause = "";
	// 业务数据
	private Object data;
	
	public ResultMessage() {
	}

	public ResultMessage(int result, String message) {
		this.result = result;
		this.message = message;
	}
	
	public ResultMessage(int result, String message, String cause) {
		this.result = result;
		this.message = message;
		this.cause = cause;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
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

	public String toString() {
		JSONStringer stringer = new JSONStringer();
		stringer.object();
		stringer.key("result");
		stringer.value(result);
		stringer.key("message");
		stringer.value(message);
		stringer.key("cause");
		stringer.value(cause);
		stringer.key("data");
		stringer.value(data);
		stringer.endObject();
		return stringer.toString();
	}

}
