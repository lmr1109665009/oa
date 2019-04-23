package com.suneee.eas.push.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.bean.push.Message;
import com.suneee.eas.common.bean.push.PushConstants;
import com.suneee.eas.common.bean.push.PushMessage;
import com.suneee.eas.common.bean.push.PushResult;
import com.suneee.eas.push.service.MessagesService;
import com.suneee.eas.push.util.HttpClientUtil;
import com.suneee.eas.push.util.PushPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @description 消息处理实体类
 * @author liuhai
 * @Create 2018/06/21
 */
@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private PushPropertiesUtil pushPropertiesUtil;

    @Override
    public PushResult pushMessageToAndroid(Message message, PushMessage pushMessage) {
        return this.push(message, pushMessage, PushConstants.Android, PushConstants.PUSH_MESSAGE);
    }

    @Override
    public PushResult pushMessageToIos(Message message, PushMessage pushMessage) {
        return this.push(message, pushMessage, PushConstants.Ios, PushConstants.PUSH_MESSAGE);
    }

    @Override
    public PushResult pushNoticeToAndroid(Message message, PushMessage pushMessage) {
        return this.push(message, pushMessage, PushConstants.Android, PushConstants.PUSH_NOTICE);
    }

    @Override
    public PushResult pushNoticeToIos(Message message, PushMessage pushMessage) {
        return this.push(message, pushMessage, PushConstants.Ios, PushConstants.PUSH_NOTICE);
    }

    /**
     * @description 推送到消息中心
     * @param message
     * @return PushResult
     */
    @Override
    public PushResult push(Message message, PushMessage pushMessage, String type, String messageType) {
        if(message == null){
            return null;
        }
        message.setType(type);
        message.setMessageType(messageType);
        if(StringUtils.isEmpty(message.getVoice())){
            message.setVoice(PushConstants.PUSH_VOICE_DEFAULT);
        }
        pushMessage.setMessage(JSONObject.toJSONString(message));

        String url = pushPropertiesUtil.getMessagesUrl()+PushConstants.OPERATION_PUSH;
        String result = HttpClientUtil.sendPostRequest(url, JSONObject.toJSONString(pushMessage));
        if(StringUtils.isNotEmpty(result)){
            PushResult pushResult = JSONObject.parseObject(result,PushResult.class);
            if(null!= pushResult.getData()){
                if(StringUtils.isNotEmpty(pushResult.getData().getMessage())) {
                    pushResult.setMessageResult(JSONObject.parseObject(pushResult.getData().getMessage(), Message.class));
                }
            }
            return pushResult;
        }
        return null;
    }

    @Override
    public Message getMessage(HttpServletRequest request) {
        String message = request.getParameter("message");
        String sendFrom = request.getParameter("sendFrom");
        String source = request.getParameter("source");
        String router = request.getParameter("router");     //跳转地址
        String voice = request.getParameter("voice");       //提示音
        String sendTo = request.getParameter("sendTo");
        String appName = request.getParameter("appName");
        String notificationType = request.getParameter("notificationType");
        String pushTime = request.getParameter("pushTime");

        //测试数据
//        message = "测试消息。。。。24324324324";
//        sendFrom = "kkkk";
//        source = "开发部";
//        router = "";
//        voice = "default";
//        sendTo = "e34c90b0bb134a29aad2c29b4efa0d47";
//        appName = "xiangpuIosMerge";
//        notificationType = "Personal";
//        pushTime = "2018-6-25 14:05:00";

        //封装消息体
        Message messages = new Message();
        messages.setMessage(message);
        messages.setSendFrom(sendFrom);
        messages.setSource(source);
        messages.setRouter(router);
        messages.setVoice(voice);
        messages.setSendTo(sendTo);
        messages.setAppName(appName);
        messages.setNotificationType(notificationType);
        messages.setPushTime(pushTime);
        return messages;
    }

    @Override
    public PushMessage getPushMessage(HttpServletRequest request) {
        String topic = request.getParameter("topic");
        String clientCode = request.getParameter("clientCode");
        //测试数据
//        topic = "app/SUNEEE";
//        clientCode = "SUNEEE";

        //包装主题
        PushMessage pushBody = new PushMessage();
        pushBody.setTopic(topic);
        pushBody.setClientCode(clientCode);
        return pushBody;
    }


}
