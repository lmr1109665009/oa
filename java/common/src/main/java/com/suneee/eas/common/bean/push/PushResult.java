package com.suneee.eas.common.bean.push;

import java.io.Serializable;

/**
 * 消息返回结果集
 * @author liuhai
 * @Create  2018/06/21
 */
public class PushResult implements Serializable{

    private String status;
    private String message;
    private Message messageResult;
    private String code;
    private MessageData data;
    private String page;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MessageData getData() {
        return data;
    }

    public void setData(MessageData data) {
        this.data = data;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Message getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(Message messageResult) {
        this.messageResult = messageResult;
    }

    @Override
    public String toString() {
        return "PushResult{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", messageResult=" + messageResult +
                ", code='" + code + '\'' +
                ", data=" + data +
                ", page='" + page + '\'' +
                '}';
    }
}
