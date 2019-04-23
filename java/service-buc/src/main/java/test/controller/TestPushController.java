package test.controller;

import com.alibaba.fastjson.JSONObject;
import com.suneee.eas.common.bean.push.Message;
import com.suneee.eas.common.bean.push.PushMessage;
import com.suneee.eas.common.bean.push.PushResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import test.service.TestService;


/**
 * 推送测试类
 *       接口请求参数：
         {
         "topic":"app/SUNEEE",
         "message":"{               ---  message的值要转成字符串
         　　"message":"收到",       ---内容
         　　"sendFrom":"张三",      ---发送人（标题）
         　　"source":"ucp",          ---来源（部门）
         　　"router":”http://www.baidu.com”,   ---跳转
         　　"voice":"default",         ---提示音
         　　"sendTo":"e34c90b0bb134a29aad2c29b4efa0d47", ---推送id,推送多个用逗号隔开
         　　"appName":"xiangpuIosMerge",  ---版本信息
             “notificationType”:”Enterprise”  ---Enterprise为企业消息，Personal为个人消息
             “pushTime”:”2018-6-25 00:00:00”  ----不添加为立即发送，添加为定时发送
         }",
         "clientCode":"SUNEEE"
         }
         接口返回数据：
         {
             "status":"200",
             "message":"success",
             "code":"0",
             "data":{
                    "id":null,
                    "topic":"app/SUNEEE",
                    "message":"{
                        \"message\":\"收到\",
                        \"sendFrom\":\"张三\",
                        \"source\":\"ucp\",
                        \"router\":{
                            \"name\":\"公共大厅@iWJsbcK4ZhAicjHtA1511427379501\",
                            \"type\":\"p\"
                        },
                        \"voice\":\"default\",
                        \"type\":\"ios\",
                        \"sendTo\":\"e34c90b0bb134a29aad2c29b4efa0d47\",
                        \"appName\":\"xiangpuIosMerge\"
                     }",
                    "operation":"app",
                    "lastUpdateDate":1522393623312,
                    "clientCode":"SUNEEE",
                    "serviceUrl":null
              },
             "page":null
         }
 */
@RestController
@RequestMapping("/pushResult")
public class TestPushController {

    @Autowired
    private TestService testService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * service调用形式推送
     */
    @PostMapping("/ios")
    public PushResult pushMessageIos(){

        PushMessage pushMessage = getTestPushMessage();
        PushResult result = testService.pushMessagesToIos(pushMessage);
        System.out.println(result.toString());
        return result;
    }

    /**
     * RestTemplate调用形式推送
     */
    @PostMapping("/tempIos")
    public PushResult pushByRestTemplate(){
        PushMessage pushMessage = getTestPushMessage();
        ResponseEntity<PushResult> responseEntity = restTemplate.postForEntity("http://localhost:8093/push/messages/ios", pushMessage, PushResult.class);
        System.out.println("statusCode:------>"+responseEntity.getStatusCodeValue());
        PushResult result = responseEntity.getBody();
        System.out.println("body:----->"+result.toString());
        return result;
    }

    /**
     * 测试数据
     */
    private PushMessage getTestPushMessage(){
        //测试数据---详细参数介绍 见顶部
        String message = "测试消息。。。。24324324324";
        String sendFrom = "kkkk";
        String source = "开发部";
        String router = "";
        String voice = "default";
        String sendTo = "e34c90b0bb134a29aad2c29b4efa0d47";
        String appName = "xiangpuIosMerge";
        String notificationType = "Personal";
        String messageType = "message";
        String pushTime = "2018-6-26 11:24:00";
        String topic = "app/SUNEEE";
        String clientCode = "SUNEEE";


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
        messages.setMessageType(messageType);
        messages.setPushTime(pushTime);

        PushMessage pushMessage = new PushMessage();
        pushMessage.setTopic(topic);
        pushMessage.setClientCode(clientCode);
        pushMessage.setMessage(JSONObject.toJSONString(messages));

        return pushMessage;
    }
}
