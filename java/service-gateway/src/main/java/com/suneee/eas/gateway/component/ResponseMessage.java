package com.suneee.eas.gateway.component;

import java.io.Serializable;

/**
 * 系统响应消息
 * @author 子华
 */
public class ResponseMessage<T> implements Serializable {
    private static final long serialVersionUID = 9214264614314532462L;
    //未登录状态
    public static final int STATUS_NOLOGIN=-1;
    //请求成功
    public static final int STATUS_SUCCESS=0;
    //请求失败
    public static final int STATUS_FAIL=1;
    private int status;
    private String message;
    private String authId;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 操作成功响应消息
     * @param message
     * @param data
     * @return
     */
    public static <T> ResponseMessage<T> success(String message,T data){
        ResponseMessage<T> respMessage=new ResponseMessage<T>();
        respMessage.setStatus(ResponseMessage.STATUS_SUCCESS);
        respMessage.setMessage(message);
        respMessage.setData(data);
        return respMessage;
    }
    public static ResponseMessage success(String message){
        return success(message,null);
    }

    /**
     * 操作失败响应消息
     * @param message
     * @param data
     * @return
     */
    public static <T> ResponseMessage<T> fail(String message,T data){
        ResponseMessage<T> respMessage=new ResponseMessage<T>();
        respMessage.setStatus(ResponseMessage.STATUS_FAIL);
        respMessage.setMessage(message);
        respMessage.setData(data);
        return respMessage;
    }
    public static ResponseMessage fail(String message){
        return fail(message,null);
    }

    /**
     * 用户未登陆响应消息
     * @param message
     * @return
     */
    public static ResponseMessage noLogin(String message){
        ResponseMessage respMessage=new ResponseMessage();
        respMessage.setStatus(ResponseMessage.STATUS_NOLOGIN);
        respMessage.setMessage(message);
        return respMessage;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
}