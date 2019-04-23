package com.suneee.eas.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.suneee.eas.common.component.MyCookieSerializer;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.UserConstant;
import com.suneee.eas.common.encrypt.EncryptUtil;
import com.suneee.eas.common.utils.HttpUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.gateway.component.AuthenticationProperty;
import com.suneee.eas.gateway.exception.UnauthorizedException;
import com.suneee.eas.gateway.security.CustomPwdEncoder;
import com.suneee.eas.gateway.service.EnterpriseInfoService;
import com.suneee.eas.gateway.service.SysUserService;
import com.suneee.eas.gateway.utils.CookieUitl;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部接口调用自动鉴权过滤器
 * @user 子华
 * @created 2018/10/11
 */
@Component
public class ApiAuthenticationFilter extends BaseZuulFilter {
    public static final String API_AUTHORIZE_TOKEN="apiAuthorizeToken";
    private static final Logger log= LogManager.getLogger(ApiAuthenticationFilter.class);
    @Autowired
    private AuthenticationProperty authenticationProperty;
    @Autowired
    private SysUserService userService;
    @Autowired
    private EnterpriseInfoService enterpriseInfoService;
    @Autowired
    private SessionRepositoryFilter sessionRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request=getRequest();
        String authorizeToken=getAuthorizeToken();
        if (StringUtil.isEmpty(authorizeToken)){
            return false;
        }
        try {
            Map<String,String> map=parseToken(authorizeToken);
            List<String> targetUrls=authenticationProperty.getAuthorizeUrls().get(map.get("from"));
            String uri= request.getRequestURI();
            if (!isMatchAuthorizeUrl(targetUrls,uri)){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 是否匹配到允许自动鉴权url地址
     * @param patternUrl
     * @param url
     * @return
     */
    private boolean isMatchAuthorizeUrl(List<String> patternUrl, String url){
        if (patternUrl==null||patternUrl.size()==0){
            return false;
        }
        AntPathMatcher pathMatcher=new AntPathMatcher();
        for (String pattern:patternUrl){
            if (pathMatcher.match(pattern,url)){
                return true;
            }
        }
        return false;
    }

    /**
     * 解析token
     * @param token
     * @return
     * @throws Exception
     */
    private Map<String,String> parseToken(String token) throws Exception {
        String content= EncryptUtil.decrypt(token);
        String[] args=content.split("\\|\\|");
        Map<String,String> map=new HashMap<>();
        map.put("from",args[0]);
        map.put("account",args[1]);
        return map;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 8;
    }

    @Override
    public Object run() throws ZuulException {
        String authorizeToken=getAuthorizeToken();
        HttpServletRequest request=getRequest();
        HttpServletResponse response=getResponse();
        String name="";
        try {
            Map<String,String> map=parseToken(authorizeToken);
            String account=map.get("account");
            // 根据账号查询系统用户信息
            SysUser sysUser = userService.getByAccount(account);

            // 鉴权用户的账号在本系统中不存在
            if (sysUser == null) {
                // 鉴权用户在本系统中不存在
                log.error("鉴权失败：账号【" + account + "】不存在");
                throw new UnauthorizedException("账号【" + account + "】不存在");
            }
            String enterpriseCode = getParamEnterpriseCode();
            Enterpriseinfo enterpriseinfo = null;
            if (StringUtils.isBlank(enterpriseCode)) {
                List<Enterpriseinfo> enterpriseinfos = enterpriseInfoService.getByUserId(sysUser.getUserId());
                if (enterpriseinfos.isEmpty()) {
                    // 用户未设置所属企业
                    log.error("鉴权失败：账号【" + account + "】未设置所属企业");
                    throw new UnauthorizedException("账号【" + account + "】未设置所属企业");
                }
                enterpriseinfo = enterpriseinfos.get(0);
                enterpriseCode = enterpriseinfo.getComp_code();
            }
            // 将企业信息设置到cookie中
            name = CookieUitl.getSwitchEntCookieName(sysUser.getUserId());
            CookieUitl.addCookieAndSession(name, enterpriseCode, -1, "/", request, response);
            if (enterpriseinfo == null) {
                enterpriseinfo = enterpriseInfoService.getByCompCode(enterpriseCode);
            }

            CustomPwdEncoder.setIngore(true);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sysUser.getAccount(), "");
            authRequest.setDetails(new WebAuthenticationDetails(request));
            SecurityContext securityContext = SecurityContextHolder.getContext();
            Authentication auth = authenticationManager.authenticate(authRequest);
            securityContext.setAuthentication(auth);
            HttpSession session = getSession();
            session.setAttribute(UserConstant.SESSION_USER_KEY, sysUser);
            session.setAttribute(UserConstant.SESSION_ENTERPRISE_KEY, enterpriseinfo);
            String authId = Base64.getEncoder().encodeToString(session.getId().getBytes());
            request.setAttribute(MyCookieSerializer.KEY_SESSION_ID, authId);
            getContext().addZuulRequestHeader(MyCookieSerializer.KEY_SESSION_ID, authId);
            sessionRepository.commitSession(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            // 鉴权用户帐号异常
            log.error("自动鉴权失败：" + e.getMessage());
            stopZuulResponse();
            try {
                HttpUtil.writeResponseJson(response, ResponseMessage.fail("自动鉴权失败：" + e.getMessage()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new ZuulRuntimeException(e);
        }
        return null;
    }

    /**
     * 获取认证授权token
     * @return
     */
    private String getAuthorizeToken(){
        HttpServletRequest request=getRequest();
        String authorizeToken=request.getHeader(API_AUTHORIZE_TOKEN);
        if (StringUtil.isNotEmpty(authorizeToken)){
            return authorizeToken;
        }
        authorizeToken=request.getParameter(API_AUTHORIZE_TOKEN);
        return authorizeToken;
    }

    /**
     * 停止zuul路由转发
     */
    private void stopZuulResponse() {
        RequestContext context = getContext();
        HttpServletRequest request = getRequest();
        log.info(String.format("%s >>> %s，自动鉴权失败，无法访问", request.getMethod(), request.getRequestURL().toString()));
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(401);
        context.setSendZuulResponse(false);
    }
}
