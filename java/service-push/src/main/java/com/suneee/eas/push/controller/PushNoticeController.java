package com.suneee.eas.push.controller;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.bean.push.Message;
import com.suneee.eas.common.bean.push.PushMessage;
import com.suneee.eas.common.bean.push.PushResult;
import com.suneee.eas.push.service.MessagesService;
import com.suneee.eas.push.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 推送通知接口
 * @author liuhai
 * @Create 2018/06/21
 */
@RestController
@RequestMapping("/push/notice")
public class PushNoticeController {

    @Autowired
    private MessagesService messagesService;

    @PostMapping("/android")
    public PushResult pushToAndroid(HttpServletRequest request){
        String params = RequestUtil.getMessage(request);
        PushMessage pushMessage = JSONObject.parseObject(params, PushMessage.class);
        Message message = null;
        if(StringUtils.isNotEmpty(pushMessage.getMessage())){
            message = JSONObject.parseObject(pushMessage.getMessage(), Message.class);
        }
        return messagesService.pushNoticeToAndroid(message, pushMessage);
    }

    @PostMapping("/ios")
    public PushResult pushToIos(HttpServletRequest request){
        String params = RequestUtil.getMessage(request);
        PushMessage pushMessage = JSONObject.parseObject(params, PushMessage.class);
        Message message = null;
        if(StringUtils.isNotEmpty(pushMessage.getMessage())){
            message = JSONObject.parseObject(pushMessage.getMessage(), Message.class);
        }
        return messagesService.pushNoticeToIos(message, pushMessage);
    }
}
