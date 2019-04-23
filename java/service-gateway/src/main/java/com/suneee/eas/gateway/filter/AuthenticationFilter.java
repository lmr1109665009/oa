package com.suneee.eas.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import com.suneee.eas.common.component.MyCookieSerializer;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.HttpConstant;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.gateway.component.AuthenticationProperty;
import com.suneee.eas.gateway.component.Result;
import com.suneee.eas.gateway.exception.UnauthorizedException;
import com.suneee.eas.gateway.security.CustomPwdEncoder;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.eas.gateway.service.SysUserService;
import com.suneee.eas.gateway.utils.Constants;
import com.suneee.eas.gateway.utils.CookieUitl;
import com.suneee.eas.gateway.utils.HttpUtils;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统认证过滤器
 *
 * @author 子华
 * 登陆认证过滤器
 */
@Component
public class AuthenticationFilter extends BaseZuulFilter {

    private static Logger log = LogManager.getLogger(AuthenticationFilter.class);
    public static final String PREFIX_AUTHORITY_TOKEN = "eas.security.authentication.token.";
    @Autowired
    private Environment env;
    @Autowired
    private SysUserService userService;
    @Autowired
    private EnterpriseInfoService enterpriseInfoService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SessionRepositoryFilter sessionRepository;
    @Autowired
    private AuthenticationProperty authenticationProperty;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request=getRequest();
        String uri= request.getRequestURI();

        AntPathMatcher pathMatcher=new AntPathMatcher();
        for (String pattern:authenticationProperty.getIgnoreUrls()){
            if (pathMatcher.match(pattern,uri)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        String runtimeEnv= (String) request.getAttribute("runtimeEnv");
        if (StringUtil.isNotEmpty(runtimeEnv)){
            getContext().addZuulRequestHeader("runtimeEnv", runtimeEnv);
        }
        String sessionId = RequestUtil.getString(request, "sessionId");
        if (StringUtil.isEmpty(sessionId)) {
            sessionId = request.getHeader("sessionId");
        }
        String authId = (String) request.getAttribute(MyCookieSerializer.KEY_SESSION_ID);
        //优先处理北京用户中心鉴权
        if (StringUtil.isEmpty(authId) && StringUtil.isNotEmpty(sessionId)) {
            try {
                return authByUcenter(sessionId);
            } catch (Exception e) {
                e.printStackTrace();
                stopZuulResponse();
                try {
                    HttpUtil.writeResponseJson(response, ResponseMessage.fail(e.getMessage()));
                    return null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }finally {
                    throw new ZuulRuntimeException(e);
                }
            }
        } else if (StringUtil.isNotEmpty(authId)) {
            getContext().addZuulRequestHeader(MyCookieSerializer.KEY_SESSION_ID, authId);
        }
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute(UserConstant.SESSION_USER_KEY);
        if (user == null) {
            stopZuulResponse();
            String msg="";
            try {
                response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
                if (!isContainSessionInCookie()) {
                    msg="请登录后再操作";
                    HttpUtil.writeResponseJson(response, ResponseMessage.noLogin(msg));
                    return null;
                }
                msg="session已失效，请重新登录";
                HttpUtil.writeResponseJson(response, ResponseMessage.noLogin(msg));
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                throw new ZuulRuntimeException(new RuntimeException(msg));
            }
        }
        return null;
    }

    /**
     * 判断是否cookie中包含session
     *
     * @return
     */
    private boolean isContainSessionInCookie() {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookieItem : cookies) {
            if ("SESSION".equals(cookieItem.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 停止zuul路由转发
     */
    private void stopZuulResponse() {
        RequestContext context = getContext();
        HttpServletRequest request = getRequest();
        log.info(String.format("%s >>> %s，未登录用户，无法访问", request.getMethod(), request.getRequestURL().toString()));
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
    }

    /**
     * 通过用户中心来进行鉴权
     *
     * @param sessionId
     * @return
     */
    private Object authByUcenter(String sessionId) throws IOException, UnauthorizedException {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        if (StringUtil.isEmpty(sessionId)) {
            HttpUtil.writeResponseJson(response, ResponseMessage.fail("鉴权失败，缺少参数sessionId"));
            return null;
        }
        doAuthWithToken(sessionId, request, response);
        return null;
    }

    /**
     * 根据用户中心token(sessionId)来进行鉴权
     *
     * @param sessionId
     * @param request
     * @param response
     * @throws UnauthorizedException
     * @throws UnsupportedEncodingException
     */
    private void doAuthWithToken(String sessionId, HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException, UnsupportedEncodingException {
        String authId;// 构造用户中心sessionId授权登陆接口的请求参数
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("sessionId", sessionId);
        dataMap.put("enterpriseCode", env.getProperty(Constants.UC_ENTERPRISECODE + "." + ContextUtil.getRuntimeEnv()));
        dataMap.put("clientIp", env.getProperty(Constants.UC_CLIENTIP));
        dataMap.put("encryptCode", env.getProperty(Constants.UC_ENCRYPTCODE));
        dataMap.put("appCode", env.getProperty(Constants.UC_APPCODE));
        // 到用户中心sessionId授权登陆接口鉴权
        Result result = null;
        try {
            result = HttpUtils.sendToUserCenter(Constants.UC_ACCESSLOGIN_API, dataMap, true);
//            result = HttpUtils.sendToUserCenter(Constants.UC_GETBYUSERANDORG_API, dataMap, true);
        } catch (Exception e) {
            log.error("鉴权失败：" + e.getMessage(), e);
            throw new UnauthorizedException("鉴权失败：" + e.getMessage());
        }

        // 接口调用异常
        if (result == null) {
            log.error("鉴权失败：接口调用异常");
            throw new UnauthorizedException("鉴权失败：接口调用异常");
        }
        Map<String, Object> ucUser = (Map<String, Object>) result.getData();
        if (ucUser == null) {
            log.error("鉴权失败：接口调用异常");
            throw new UnauthorizedException("鉴权失败：接口调用异常");
        }
        // 鉴权失败
        if (!Constants.UC_API_SUCCESS.equals(result.getStatus())) {
            // 用户中心鉴权失败
            log.error("接口鉴权失败：" + result.getMessage());
            throw new UnauthorizedException("接口鉴权失败：" + result.getMessage());
        }
        if ("customer".equals(request.getParameter("flag"))) {

        } else {
            // 根据用户中心的账号查询本系统中的用户信息

            String account = (String) ucUser.get("account");

            // 根据账号查询系统用户信息
            SysUser sysUser = userService.getByAccount(account);

            // 鉴权用户的账号在本系统中不存在
            if (sysUser == null) {
                // 鉴权用户在本系统中不存在
                log.error("鉴权失败：账号【" + account + "】不存在");
                throw new UnauthorizedException("鉴权失败：账号【" + account + "】不存在");
            }
            String enterpriseCode = getParamEnterpriseCode();
            Enterpriseinfo enterpriseinfo = null;
            if (StringUtils.isBlank(enterpriseCode)) {
                List<Enterpriseinfo> enterpriseinfos = enterpriseInfoService.getByUserId(sysUser.getUserId());
                if (enterpriseinfos.isEmpty()) {
                    // 用户未设置所属企业
                    log.error("鉴权失败：账号【" + account + "】未设置所属企业");
                    throw new UnauthorizedException("鉴权失败：账号【" + account + "】未设置所属企业");
                }
                enterpriseinfo = enterpriseinfos.get(0);
                enterpriseCode = enterpriseinfo.getComp_code();
            }
            // 将企业信息设置到cookie中
            String name = CookieUitl.getSwitchEntCookieName(sysUser.getUserId());
            CookieUitl.addCookieAndSession(name, enterpriseCode, -1, "/", request, response);
            if (enterpriseinfo == null) {
                enterpriseinfo = enterpriseInfoService.getByCompCode(enterpriseCode);
            }

            try {
                CustomPwdEncoder.setIngore(true);
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sysUser.getAccount(), "");
                authRequest.setDetails(new WebAuthenticationDetails(request));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication auth = authenticationManager.authenticate(authRequest);
                securityContext.setAuthentication(auth);
                HttpSession session = getSession();
                session.setAttribute(UserConstant.SESSION_USER_KEY, sysUser);
                session.setAttribute(UserConstant.SESSION_ENTERPRISE_KEY, enterpriseinfo);
                authId = Base64.getEncoder().encodeToString(session.getId().getBytes());
                HashMap<String, Object> tokenBody = new HashMap<>();
                tokenBody.put(MyCookieSerializer.KEY_SESSION_ID, authId);
                tokenBody.put("enterpriseCode", enterpriseCode);
                redisTemplate.opsForValue().set(PREFIX_AUTHORITY_TOKEN + sessionId, tokenBody, 1800, TimeUnit.SECONDS);
                request.setAttribute(MyCookieSerializer.KEY_SESSION_ID, authId);
                getContext().addZuulRequestHeader(MyCookieSerializer.KEY_SESSION_ID, authId);
                sessionRepository.commitSession(request, response);
            } catch (AuthenticationException e) {
                // 鉴权失败，删除企业cookie信息
                CookieUitl.addCookie(name, "", 0, "/", request, response);
                // 鉴权用户帐号异常
                log.error("鉴权失败：" + e.getMessage());
                e.printStackTrace();
                throw new UnauthorizedException("鉴权失败：" + e.getMessage());
            }

        }

    }
}
