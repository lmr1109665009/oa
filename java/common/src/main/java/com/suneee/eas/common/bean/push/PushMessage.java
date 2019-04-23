package com.suneee.eas.common.bean.push;

import java.io.Serializable;

/**
 * 推送消息类主题
 * @author liuhai
 * @Create  2018/06/21
 */
public class PushMessage implements Serializable{
    private String topic;
    private String message;
    private String clientCode;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                ", clientCode='" + clientCode + '\'' +
                '}';
    }
}
