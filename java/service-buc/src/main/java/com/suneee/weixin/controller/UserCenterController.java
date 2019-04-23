package com.suneee.weixin.controller;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.extentity.Result;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/weixin/userCenter/")
public class UserCenterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCenterController.class);

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("getCenterUser")
    @ResponseBody
    public ResultVo getCenterUser(HttpServletRequest request, HttpServletResponse response) {

        String sessionId = request.getParameter("sessionId");
        String enterprisecode = request.getParameter("enterprisecode");
        if (StringUtil.isEmpty(sessionId)) {
            sessionId = request.getHeader("sessionId");
        }
        if (StringUtil.isNotEmpty(sessionId)) {
            //调用用户中心接口获取用户及组织信息
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("sessionId", sessionId);
            Result result = null;
            try {
                result = HttpUtils.sendToUserCenter(Constants.UC_GETBYUSERANDORG_API, dataMap, true);
                // 根据用户中心的账号查询本系统中的用户信息
                if (result == null) {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户未登陆,请重新登录");
                }
                Map<String, Object> ucUser = (Map<String, Object>) result.getData();
                if (ucUser == null || ucUser.isEmpty()) {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户未登陆,请重新登录");
                }
                Object user1 = ucUser.get("user");
                if (user1 == null) {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户未登陆,请重新登录");
                }
                Map<String, List<Map<String, Object>>> datePower = (Map<String, List<Map<String, Object>>>) ucUser.get("dataPower");
               /* if (datePower != null || !datePower.isEmpty()) {
                    List<Map<String, Object>> deptRole = datePower.get("deptRole");
                    if (deptRole == null || deptRole.size() == 0) {

                    }
                } else {
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "无权限访问,请为用户分配组织");
                }*/

                Map<String, Object> user = (Map<String, Object>) user1;
                //根据账号去本地查询是否有此用户
                String account = user.get("account").toString();

               /* SysUser sysUser = sysUserService.getByAccount(account);
                SysUser bucsysUser = new SysUser();
                //用户数据在buc不存在
                if (sysUser == null) {
                    bucsysUser.setUserId(UniqueIdUtil.genId());
                    bucsysUser.setAccount(account);//账号
                    if (user.get("aliadname") != null) {
                        bucsysUser.setAliasName(user.get("aliadname").toString());//字号
                    }
                    if (user.get("phone") != null) {
                        bucsysUser.setPhone(user.get("phone").toString());//手机
                    }
                    if ((Boolean) user.get("sex")) {//性别
                        bucsysUser.setSex("1");
                    } else {
                        bucsysUser.setSex("0");
                    }
                    if (user.get("mobile") != null) {//电话
                        bucsysUser.setMobile(user.get("mobile").toString());
                    }
                    if (user.get("email") != null) {//邮箱
                        bucsysUser.setEmail(user.get("email").toString());
                    }
                    if (user.get("name") != null) {//姓名
                        bucsysUser.setFullname(user.get("name").toString());
                    }
                    if (user.get("userId") != null) {//用户中心id
                        bucsysUser.setUcUserid(Long.parseLong(user.get("userId").toString()));
                    }
                    if (user.get("password") != null) {//密码
                        bucsysUser.setPassword(user.get("password").toString());
                    }
                    if (user.get("registerTime") != null) {//创建时间
                        Timestamp registerTime = new Timestamp(Long.parseLong(user.get("registerTime").toString()));
                        Date date = registerTime;
                        bucsysUser.setCreatetime(date);
                    }

                    // 默认角色名称
                    String defaultRoleName = AppConfigUtil.get(Constants.USER_DEFAULT_ROLENAME, "普通员工");
                    // 默认密码
                    String defaultPassword = AppConfigUtil.get(Constants.USER_DEFAULT_PASSWORD, "111111");
                    // 员工状态
                    String defaultUserStatus = AppConfigUtil.get(Constants.USER_DEFAULT_USERSTATUS, "正式员工");
                    // 是否过期
                    Short defaultIsExpired = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_ISEXPIRED, "0"));
                    // 是否锁定
                    Short defaultIsLock = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_ISLOCK, "0"));
                    // 当前状态
                    Short defaultStatus = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_STATUS, "1"));
                    // 默认组织维度：行政维度
                    Long demId = 1L;
                    bucsysUser.setIsLock(defaultIsLock);
                    bucsysUser.setStatus(defaultStatus);
                    bucsysUser.setIsExpired(defaultIsExpired);
                    bucsysUser.setUserStatus(defaultUserStatus);
                    //保存用户信息
                    sysUserService.add(bucsysUser);
                    Map<String, Object> param = new HashMap();

                    for (Map<String, Object> deptRole : deptRoles) {
                        param.put("userPosId", UniqueIdUtil.genId());
                        param.put("orgId", deptRole.get("org_id"));
                        param.put("posId", deptRole.get("pos_id"));
                        param.put("jobId", deptRole.get("job_id"));
                        param.put("userId", bucsysUser.getUserId());
                        sysUserService.setUserPosOrg(param);
                    }
                }*/

                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "用户已登录,获取用户信息成功", result);
            } catch (Exception e) {
                LOGGER.error("系统异常：" + e.getMessage(), e);
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户未登陆,请重新登录");
            }

        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "用户未登陆,请重新登录");
    }


}
