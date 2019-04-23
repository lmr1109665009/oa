package com.suneee.oa.controller.bpm;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/bpm/BpmUserAndOrg/")
public class BpmUserAndOrgApiController extends MobileBaseController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysOrgService sysOrgService;
    /**
     * 流程获取根据账号获取组织和用户信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getByOrg")
    @ResponseBody
    public ResultVo getByorg(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = new HashMap();
        String account = request.getParameter("account");
        if(StringUtil.isEmpty(account)){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数不能为空");
        }
        SysUser user = sysUserService.getByAccount(account);
        SysOrg org=null;
        if(user!=null){
            org = sysOrgService.getPrimaryOrgByUserId(user.getUserId());
        }
        map.put("sysUser",user);
        map.put("sysOrg",org);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户组织信息成功",map);
    }
}
