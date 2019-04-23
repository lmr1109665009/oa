package com.suneee.eas.push.service;

import com.suneee.eas.common.bean.push.Message;
import com.suneee.eas.common.bean.push.PushMessage;
import com.suneee.eas.common.bean.push.PushResult;

import javax.servlet.http.HttpServletRequest;

/**
 *阿里云推送处理中心
 */
public interface MessagesService {

    /**
     * 推送消息到安卓设备
     * **/
    PushResult pushMessageToAndroid(Message message, PushMessage pushMessage);

    /**
     * 推送消息到苹果设备
     * **/
    PushResult pushMessageToIos(Message message, PushMessage pushMessage);

    /**
     * 推送通知到安卓设备
     * **/
    PushResult pushNoticeToAndroid(Message message, PushMessage pushMessage);

    /**
     * 推送通知到苹果设备
     * **/
    PushResult pushNoticeToIos(Message message, PushMessage pushMessage);

    /**
     * 推送主体
     * **/
    PushResult push(Message message, PushMessage pushMessage, String type, String messageType);

    /**
     * 封装消息
     * **/
    Message getMessage(HttpServletRequest request);

    /**
     * 封装主题
     * **/
    PushMessage getPushMessage(HttpServletRequest request);
}
