package com.suneee.ucp.base.vo;

public class MsgData {

    private String unique;

    private String system;

    private String topic;

    private String operation;

    private String send;

    private String deviceToken;

    private String receiveGroup;

    private Object data;

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getReceiveGroup() {
        return receiveGroup;
    }

    public void setReceiveGroup(String receiveGroup) {
        this.receiveGroup = receiveGroup;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MsgData{" +
                "unique='" + unique + '\'' +
                ", system='" + system + '\'' +
                ", topic='" + topic + '\'' +
                ", operation='" + operation + '\'' +
                ", send='" + send + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", receiveGroup='" + receiveGroup + '\'' +
                ", data=" + data +
                '}';
    }
}
