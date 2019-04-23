package com.suneee.ucp.base.util;

import com.suneee.buc.controller.SysOrgApiController;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.vo.MsgData;
import com.suneee.ucp.base.vo.TopicsRecord;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


public class SendMsgCenterUtils {
    private Logger log= LogManager.getLogger(SendMsgCenterUtils.class);
    private static final String MESSAGE_CENTER_API_URL = AppConfigUtil.get(Constants.MESSAGE_CENTER_API_URL);
//    private static final String MESSAGE_USER_TOPIC = AppConfigUtil.get(Constants.MESSAGE_USER_TOPIC);
//    private static final String MESSAGE_ORG_TOPIC = AppConfigUtil.get(Constants.MESSAGE_ORG_TOPIC);
    private static final String MESSAGE_RECEIVECROUP_TOPIC = AppConfigUtil.get(Constants.MESSAGE_RECEIVECROUP_TOPIC);
    private static final String MESSAGE_SEND= Constants.MESSAGE_SEND;
    private static final String MESSAGE_SYSTEM= Constants.MESSAGE_SYSTEM;

    /**
     * 发送消息到框架内部消息中心
     * @param
     * @param  请求参数
     * @return
     * @throws IOException
     */
    public ResponseEntity<TopicsRecord> sendToUserInfoCenter(Object object, String stauts,String topic) throws IOException {
        ResponseEntity<TopicsRecord> responseEntity = null;
        RestTemplate restTemplate = new RestTemplate();
        if (object != null) {
            TopicsRecord topicsRecord = setObject(object,topic,stauts);
            log.info(topicsRecord.toString());
            responseEntity = restTemplate.postForEntity(MESSAGE_CENTER_API_URL, topicsRecord, TopicsRecord.class);

        }
        return responseEntity;
    }

    public ResponseEntity<TopicsRecord> sendToUserInfoCenter(Map map, String stauts,String topic) throws IOException {
        ResponseEntity<TopicsRecord> responseEntity = null;
        RestTemplate restTemplate = new RestTemplate();
        if (!map.isEmpty()) {
            TopicsRecord topicsRecord = setObject(map,topic,stauts);
            responseEntity = restTemplate.postForEntity(MESSAGE_CENTER_API_URL, topicsRecord, TopicsRecord.class);

        }
        return responseEntity;
    }

    public  TopicsRecord setObject(Object object,String topic,String status){
        String unique = UUID.randomUUID().toString();
        if(object!=null){
            MsgData msgData = new MsgData();
            msgData.setReceiveGroup(MESSAGE_RECEIVECROUP_TOPIC);
            msgData.setTopic(topic);
            msgData.setOperation(status);
            msgData.setSend(MESSAGE_SEND);
            msgData.setSystem(MESSAGE_SYSTEM);
            msgData.setUnique(unique);
            msgData.setData(object);
            String jsonMsgData = JSONObject.fromObject(msgData).toString();
            TopicsRecord topicsRecordVo = new TopicsRecord();
            topicsRecordVo.setReceiveGroup(MESSAGE_RECEIVECROUP_TOPIC);
            topicsRecordVo.setTopic(topic);
            topicsRecordVo.setSend(MESSAGE_SEND);
            topicsRecordVo.setOperation(status);
            topicsRecordVo.setSystem(MESSAGE_SYSTEM);
            topicsRecordVo.setMsgData(jsonMsgData);//MESSAGE_ENTERPRISE_CODE
            topicsRecordVo.setUnique(unique);
            topicsRecordVo.setEnterpriseCode(AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE));
            return topicsRecordVo;
        }
        return null;
    }
}
