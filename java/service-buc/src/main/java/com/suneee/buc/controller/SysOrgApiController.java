package com.suneee.buc.controller;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户组织api控制器
 * @user 子华
 * @created 2018/10/9
 */
@RestController
@RequestMapping("/api/org/")
public class SysOrgApiController {
    private Logger log= LogManager.getLogger(SysOrgApiController.class);
    @Autowired
    private SysOrgService orgService;

    /**
     * 根据用户ID获取用户主组织
     * @param userId
     * @return
     */
    @RequestMapping("getPrimaryOrgByUserId")
    public SysOrg getPrimaryOrgByUserId(Long userId){
        SysOrg org =orgService.getPrimaryOrgByUserId(userId);
        return org;
    }
}
