/**
 *
 */
package com.suneee.ucp.base.vo;

import com.suneee.ucp.base.common.ResultConst;

/**
 * @author Administrator
 */
public class ResultVo implements ResultConst {
    /**
     * 接口调用是否成功：0-成功，1-异常
     */
    private int status;

    /**
     * 信息描述
     */
    private String message;

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    private String authId;


    /**
     * 错误编码
     */
    private String code;

    /**
     * 业务信息对象
     */
    private Object data;

    public ResultVo() {

    }

    public ResultVo(Object data) {
        this.data = data;
        this.status = ResultConst.COMMON_STATUS_SUCCESS;
    }

    /**
     * @param status  接口调用是否成功
     * @param message 信息描述
     */
    public ResultVo(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * @param status  接口调用是否成功
     * @param message 信息描述
     * @param data    业务信息对象
     */
    public ResultVo(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * @param status  接口调用是否成功
     * @param message 信息描述
     * @param code    错误编码
     * @param data    业务信息对象
     */
    public ResultVo(int status, String message, String code, Object data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }
    /**
     * @param status  接口调用是否成功
     * @param message 信息描述
     * @param code    错误编码
     * @param data    业务信息对象
     */
    public ResultVo(int status,String authId, String message, String code, Object data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
        this.authId=authId;
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
        builder.append("ResultVo [status=");
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

    /**
     * 设置成功状态
     */
    public void setSuccess() {
        this.status = COMMON_STATUS_SUCCESS;
    }

    /**
     * 设置失败状态
     */
    public void setFaile() {
        this.status = COMMON_STATUS_FAILED;
    }


    public static ResultVo getSuccessInstance() {
        return new ResultVo(COMMON_STATUS_SUCCESS, "操作成功");
    }


}
