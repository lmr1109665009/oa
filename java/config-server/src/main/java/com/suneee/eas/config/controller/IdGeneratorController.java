package com.suneee.eas.config.controller;

import com.suneee.eas.common.component.SnowflakeIdWorker;
import com.suneee.eas.common.constant.ModuleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 分布式ID生成器
 * @user 子华
 * @created 2018/7/30
 */
@RestController
@RequestMapping(ModuleConstant.COMMON_MODULE)
public class IdGeneratorController {
    @Autowired
    private SnowflakeIdWorker idWorker;

    @RequestMapping("/nextId")
    public Long nextId(){
        return idWorker.nextId();
    }

}
