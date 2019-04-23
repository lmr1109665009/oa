package com.suneee.eas.common.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/** 自定义Cookie序列化类
 * @user 子华
 * @created 2018/8/24
 */
public class MyCookieSerializer extends DefaultCookieSerializer {
    public static final String KEY_SESSION_ID="authId";
    @Override
    public List<String> readCookieValues(HttpServletRequest request) {
        //优先从request请求域取sessionId参数
        String sessionIdVal= (String) request.getAttribute(KEY_SESSION_ID);
        List<String> matchingCookieValues=new ArrayList<>();
        parseSessionId(matchingCookieValues,sessionIdVal);
        if (matchingCookieValues.size()>0){
            return matchingCookieValues;
        }
        //从get/post中获取sessionId参数
        sessionIdVal= request.getParameter(KEY_SESSION_ID);
        parseSessionId(matchingCookieValues,sessionIdVal);
        if (matchingCookieValues.size()>0){
            return matchingCookieValues;
        }
        //从请求头里面获取sessionId参数
        sessionIdVal=request.getHeader(KEY_SESSION_ID);
        parseSessionId(matchingCookieValues,sessionIdVal);
        if (matchingCookieValues.size()>0){
            return matchingCookieValues;
        }
        return super.readCookieValues(request);
    }

    /**
     * 解析sessionId
     * @param matchingCookieValues
     * @param sessionIdVal
     */
    private void parseSessionId(List<String> matchingCookieValues,String sessionIdVal){
        if (StringUtils.isNotEmpty(sessionIdVal)){
            String sessionId = new String(Base64.getDecoder().decode(sessionIdVal));
            if (StringUtils.isNotEmpty(sessionId)) {
                matchingCookieValues.add(sessionId);
            }
        }
    }
}
