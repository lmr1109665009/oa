package com.suneee.eas.flowable.controller;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.flowable.service.ModelsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @user 子华
 * @created 2018/9/7
 */
@RestController
@RequestMapping("/app/rest/models/")
public class ModelsApiController  {
    private static final Logger log= LogManager.getLogger(ModelsApiController.class);
    @Autowired
    private ModelsService modelsService;

    /**
     * 发布流程定义
     * @param modelId
     * @return
     */
    @RequestMapping(value = "publish/{modelId}",method = RequestMethod.GET)
    public ResponseMessage publish(@PathVariable String modelId){
        modelsService.publish(modelId);
        return ResponseMessage.success("发布成功");
    }
}
