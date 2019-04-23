package com.suneee.platform.controller.mobile;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.core.api.util.ContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户中心控制器
 * @author hercules
 * 开发公司:深圳象羿
 */
@Controller
@RequestMapping("/platform/mobile/userCenter/")
public class MobileUserCenterController {
    @Resource
    private SysOrgService orgService;
    /**
     * 获取当前用户登录信息
     * @return
     */
    @RequestMapping("userInfo")
    @Action(description = "获取用户信息",ownermodel= SysAuditModelType.USER_MANAGEMENT,exectype="管理日志")
    @ResponseBody
    public Object userInfo(){
        JSONObject data=new JSONObject();
        SysUser user= (SysUser) ContextUtil.getCurrentUser();
        List<SysOrg> orgList=orgService.getOrgsByUserId(user.getUserId());
        if (orgList!=null&&orgList.size()>0){
            SysOrg sysOrg=orgList.get(0);
            data.put("orgName",sysOrg.getOrgName());
            data.put("orgId",sysOrg.getOrgId());
        }else {
            data.put("orgName","");
            data.put("orgId",-1);
        }
        data.put("userId",user.getUserId());
        data.put("aliasName",user.getAliasName());
        data.put("mobile",user.getMobile());
        data.put("username",user.getUsername());
        return data;
    }
}
