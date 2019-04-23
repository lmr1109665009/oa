package com.suneee.eas.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.suneee.eas.common.constant.GatewayConstant;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.encrypt.EncryptUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.gateway.component.ResponseMessage;
import com.suneee.eas.gateway.security.UserPrincipal;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.eas.gateway.service.PositionService;
import com.suneee.eas.gateway.utils.CookieUitl;
import com.suneee.eas.gateway.utils.SecurityUtil;

import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.model.system.Position;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 用户相关控制器
 *
 * @user 子华
 * @created 2018/9/12
 */
@RestController
@RequestMapping(GatewayConstant.MODULE_USER + "/")
public class UserAuthenticateController {
    @Autowired
    private EnterpriseInfoService enterpriseInfoService;
    @Autowired
    private SessionAuthenticationStrategy sessionStrategy;

    @Autowired
    private PositionService positionService;

    @RequestMapping("login")
    public ResponseMessage login(HttpServletRequest request, HttpServletResponse response) {
        String rememberMe = RequestUtil.getString(request, "rememberMe", "0");
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            if (StringUtil.isEmpty(username) && StringUtil.isEmpty(password)) {
                String requestResult = RequestUtil.getMessage(request);
                JSONObject jsonObject = JSONObject.parseObject(requestResult);
                username = jsonObject.getString("username");
                password = jsonObject.getString("password");
            }
            String encrptPassword = EncryptUtil.encrypt32MD5(password);
            Authentication auth = SecurityUtil.login(request, username, password, false);
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            SysUser user = userPrincipal.getUser();
            if (!encrptPassword.equals(userPrincipal.getPassword())) {
                throw new AuthenticationException("用户名或密码不正确！");
            }
            // 获取用户的所属企业
            List<Enterpriseinfo> enterpriseinfoList = enterpriseInfoService.getByUserId(user.getUserId());
            if (enterpriseinfoList.isEmpty()) {
                throw new AuthenticationException("请为用户分配企业！");
            }
            // 将用户的当前企业取所属企业的第一个，并保存到cookie中
            CookieUitl.addCookieAndSession(CookieUitl.getSwitchEntCookieName(user.getUserId()), enterpriseinfoList.get(0).getComp_code(), -1, "/", request, response);
            request.getSession().setAttribute(UserConstant.SESSION_ENTERPRISE_KEY, enterpriseinfoList.get(0));
            //设置从微信端登录，方便退出时退出到指定的登录页面。
            String loginAction = RequestUtil.getString(request, "flag");
            if (StringUtils.isBlank(loginAction)) {
                loginAction = "weixin";
            }
            CookieUitl.addCookie("loginAction", loginAction, request, response);
            sessionStrategy.onAuthentication(auth, request, response);
            if ("1".equals(rememberMe)) {
                SecurityUtil.writeRememberMeCookie(request, response, username, encrptPassword);
            }
            user.setPassword(null);// 返回给客户端的用户信息去掉密码

            user.setEnterpriseinfo(enterpriseinfoList.get(0));
            Position job  = enterpriseInfoService.getOrgByUserId(user.getUserId());
            if(job!=null){
                user.setPosName(job.getPosName());
                user.setOrgName(job.getOrgName());
                user.setJobName(job.getJobName());
                user.setPosId(job.getPosId());
                user.setOrgId(job.getOrgId());
                List list = new ArrayList();
                list.add(job);
                user.setDeptRole(list);
            }
           /* if (result != null) {
                List orgByUserId = result.get("toUIdByOrg");
                if (orgByUserId.size() > 0 && orgByUserId != null) {
                    Map map = (Map) orgByUserId.get(0);
                    user.setPosName(String.valueOf(map.get("POSNAME")));
                    user.setOrgName(String.valueOf(map.get("orgName")));
                    user.setJobName(String.valueOf(map.get("JOBNAME")));
                   // String posId = (String);
                    user.setPosId(Long.parseLong(String.valueOf(map.get("POSID"))));
                   // String orgId = (String);
                    user.setOrgId(Long.parseLong(String.valueOf(map.get("ORGID"))));
                    user.setDeptRole(orgByUserId);
                }
                user.setHeadImgPath(String.valueOf(result.get("headImgPath").get(0)));
            }*/
            ResponseMessage message = ResponseMessage.success("登陆成功", user);

            //用户信息保存在session中
            request.getSession().setAttribute(UserConstant.SESSION_USER_KEY, user);
            String authId = Base64.getEncoder().encodeToString(request.getSession().getId().getBytes());
            message.setAuthId(authId);
            return message;
        } catch (LockedException e) {
            return ResponseMessage.fail("该用户已被锁定，请联系管理员！");
        } catch (DisabledException e) {
            return ResponseMessage.fail("该用户已被禁用，请联系管理员！");
        } catch (AccountExpiredException e) {
            return ResponseMessage.fail("该用户已过期，请联系管理员！");
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseMessage.fail("用户名或密码不正确！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseMessage.fail(ex.getMessage());
        }
    }

}
