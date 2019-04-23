/**
 * 
 */
package com.suneee.ucp.base.extentity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6435235261686550182L;

	/**
	 * 是否成功
	 */
	private String status;
	
	/**
	 * 信息描述
	 */
	private String message;
	
	/**
	 * 错误编码
	 */
	private String code;
	
	/**
	 * 业务对象
	 */
	private  Object data;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append("[status=");
		builder.append(status);
		builder.append(", message=");
		builder.append(message);
		builder.append(", code=");
		builder.append(code);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
