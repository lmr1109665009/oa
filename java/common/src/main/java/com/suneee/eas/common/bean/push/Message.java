package com.suneee.eas.common.bean.push;

import java.io.Serializable;

/**
 * 推送消息体
 * @author liuhai
 * @Create 2018/06/21
 */
public class Message implements Serializable{

    private String message;
    private String sendFrom;
    private String source;
    private String router;
    private String voice;
    private String type;
    private String sendTo;
    private String appName;
    private String messageType;
    private String notificationType;
    private String pushTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", sendFrom='" + sendFrom + '\'' +
                ", source='" + source + '\'' +
                ", router='" + router + '\'' +
                ", voice='" + voice + '\'' +
                ", type='" + type + '\'' +
                ", sendTo='" + sendTo + '\'' +
                ", appName='" + appName + '\'' +
                ", messageType='" + messageType + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", pushTime='" + pushTime + '\'' +
                '}';
    }
}
