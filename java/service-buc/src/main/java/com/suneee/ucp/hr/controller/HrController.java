package com.suneee.ucp.hr.controller;


import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.UserPositionExtendService;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.extentity.Result;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.util.SendMsgCenterUtils;
import com.suneee.ucp.base.vo.ResultVo;
import freemarker.template.TemplateException;
import net.sf.excelutils.tags.ForeachTag;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/hr/")
public class HrController {


    @Resource
    private SysUserService sysUserService;
    @Resource
    private EnterpriseinfoService enterpriseinfoService;
    @Resource
    private SysOrgExtService sysOrgExtService;
    @Resource
    private UserPositionExtendService  userPositionExtendService;

    @RequestMapping("getSysUser")
    @ResponseBody
    public ResultVo login(HttpServletRequest request, HttpServletResponse response) {
        String enterpriseCode = request.getHeader("enterpriseCode");

        if (StringUtil.isEmpty(enterpriseCode)) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "企业编码不能为空");
        }

        Long userId = ContextUtil.getCurrentUserId();
        // Map<String,Object> result =new HashMap<String, Object>();
        if (userId != 0L && userId != null) {
            SysUser sysUser = sysUserService.getByUserId(userId);
            if (sysUser == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "当前用户不存在,请重新登录");
            }
            sysUser.setHeadImgPath(FileUtil.getDownloadUrl(sysUser.getHeadImgPath(), false));
            //result.put("user",sysUser);
            if (sysUser != null) {
                //获取用户所属企业
                List<Enterpriseinfo> enterpriseinfoList = enterpriseinfoService.getByUserId(userId);
                if (enterpriseinfoList == null || enterpriseinfoList.size() == 0) {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请为当前用户分配企业");
                }
                for (Enterpriseinfo enterpriseinfo : enterpriseinfoList) {
                    if (enterpriseCode.equals(enterpriseinfo.getComp_code())) {
                        sysUser.setEnterpriseinfo(enterpriseinfo);
                        break;
                    }
                }
                if (sysUser.getEnterpriseinfo() == null) {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "当前企业不存在此用户");
                }

            }
            List toUIdByOrg = sysOrgExtService.getToUIdByOrg(userId);
            sysUser.setDeptRole(toUIdByOrg);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息成功", sysUser);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取异常,请重新登录");
    }
    /**
     * 大行政修改用户基本信息
     * 
     *
     * */
    @RequestMapping("updateSysUser")
    @ResponseBody
    public ResultVo updateSysUser(HttpServletRequest request, HttpServletResponse response) {

        String requestResult = com.suneee.core.web.util.RequestUtil.getMessage(request);
        SysUser sysUser = JSONObject.parseObject(requestResult, SysUser.class);
        Boolean aBoolean = false;
        try {
            aBoolean = sysUserService.updateBySysUser(sysUser);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "设置失败");
        }
        if (aBoolean) {
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "设置成功");
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "设置失败");
    }
}
