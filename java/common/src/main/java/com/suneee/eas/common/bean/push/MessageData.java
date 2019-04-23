package com.suneee.eas.common.bean.push;

import java.io.Serializable;

/**
 * 返回接收消息类
 * @author liuhai
 * @Create 2018/06/21
 */
public class MessageData implements Serializable{
    private String id;
    private String topic;
    private String message;
    private String operation;
    private long lastUpdateDate;
    private String clientCode;
    private String serviceUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                ", operation='" + operation + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", clientCode='" + clientCode + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                '}';
    }
}
