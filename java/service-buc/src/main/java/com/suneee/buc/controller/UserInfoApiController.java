package com.suneee.buc.controller;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.extentity.Result;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息控制器
 *
 * @user 子华
 * @created 2018/9/17
 */
@RestController
@RequestMapping("/api/user/")
public class UserInfoApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoApiController.class);
    @Autowired
    private SysOrgService orgService;
    @Resource
    private SysOrgExtService sysOrgExtService;

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @RequestMapping("getCurrentUser")
    public ResponseMessage getCurrentUser(HttpServletRequest request) {
        String sessionId = request.getParameter("sessionId");
        if (com.suneee.core.util.StringUtil.isEmpty(sessionId)) {
            sessionId = request.getHeader("sessionId");
        }

        if (StringUtil.isNotEmpty(sessionId)) {
            //调用用户中心接口获取用户及组织信息
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("sessionId", sessionId);
            Result result = null;
            try {
                result = HttpUtils.sendToUserCenter(Constants.UC_GETBYUSERANDORG_API, dataMap, true);
                return ResponseMessage.success("获取当前用户信息成功", result);
            } catch (Exception e) {
                LOGGER.error("系统异常：" + e.getMessage(), e);
                return ResponseMessage.fail("系统异常");
            }
        } else {
            SysUser user = ContextSupportUtil.getCurrentUser();
            user.setEnterpriseinfo(ContextSupportUtil.getCurrentEnterprise());
            user.setHeadImgPath(FileUtil.getDownloadUrl(user.getHeadImgPath(), false));
            List toUIdByOrg = sysOrgExtService.getToUIdByOrg(user.getUserId());
            user.setDeptRole(toUIdByOrg);
            return ResponseMessage.success("获取当前用户信息成功", user);

        }

    }

}
