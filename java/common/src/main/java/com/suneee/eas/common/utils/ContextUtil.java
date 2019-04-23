package com.suneee.eas.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 系统上下文工具类
 * @user 子华
 * @created 2018/7/31
 */
@Component
public class ContextUtil {
    private static Environment env;

    @Autowired
    public void setEnv(Environment env) {
        ContextUtil.env = env;
    }

    /**
     * 获取请求holder
     * @return
     */
    private static ServletRequestAttributes getContextHolder(){
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取当前request对象
     * @return
     */
    public static HttpServletRequest getRequest(){
        ServletRequestAttributes context=getContextHolder();
        if (context==null){
            return null;
        }
        return context.getRequest();
    }

    /**
     * 获取当前response对象
     * @return
     */
    public static HttpServletResponse getResponse(){
        ServletRequestAttributes context=getContextHolder();
        if (context==null){
            return null;
        }
        return context.getResponse();
    }

    /**
     * 获取当前session
     * @return
     */
    public static HttpSession getSession(){
        HttpServletRequest request=getRequest();
        if (request==null){
            return null;
        }
        return request.getSession();
    }

    /**
     * 获取系统配置
     * @param key
     * @param defaultVal
     * @return
     */
    public static String getSysConfig(String key,String defaultVal){
        return env.getProperty(key,defaultVal);
    }
    public static String getSysConfig(String key){
        return env.getProperty(key);
    }

    /**
     * 获取系统运行环境
     * @return
     */
    public static String getRuntimeEnv(){
        HttpServletRequest request=getRequest();
        if (request!=null){
            String runtimeEnv= (String) request.getAttribute("runtimeEnv");
            if (StringUtil.isNotEmpty(runtimeEnv)){
                return runtimeEnv;
            }
            runtimeEnv=request.getHeader("runtimeEnv");
            if (StringUtil.isNotEmpty(runtimeEnv)){
                return runtimeEnv;
            }
        }
        return getSysConfig("system.runtimeEnv","");
    }

    /**
     *  是否为ajax请求
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request){
        String header = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(header) ? true:false;
        return isAjax;
    }
}
