package com.suneee.eas.gateway.utils;

import com.suneee.eas.common.utils.ContextSupportUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;


/**
 * 
 * cookie操作类
 *<br>功能：对cookie进行增查改删
 */
public class CookieUitl
{
    /**
     * 往客户端写入Cookie。
     * <pre>
     * 说明:
     * maxAge:(单位秒)
     * 0:删除Cookie
     * -1:页面关闭时删除cookie
     * </pre>
     * @param name String
     * @param value String
     * @param maxAge int
     * @param context PageContext
     */
    public static void addCookie(String name,String value,int maxAge,PageContext context)
    {
        HttpServletResponse response=(HttpServletResponse)context.getResponse();
        HttpServletRequest req=(HttpServletRequest)context.getRequest();
        addCookie( name, value,maxAge,  req,  response);
    }
    
    /**
     * 写入会话cookie，在页面关闭的时候这个cookie自动删除。
     * @param name		cookie名称
     * @param value		cookie值。
     * @param context
     */
    public static void addCookie(String name,String value,PageContext context)
    {
    	HttpServletResponse response=(HttpServletResponse)context.getResponse();
    	HttpServletRequest req=(HttpServletRequest)context.getRequest();
    	addCookie( name, value,-1,  req,  response);
    }
    
    /**
     * 添加cookie，cookie的生命周期为关闭浏览器即消失
     * @param name
     * @param value
     * @param req
     * @param response
     */
    public static void addCookie(String name,String value,HttpServletRequest req,HttpServletResponse response)
    {
        addCookie( name, value,-1,  req,  response);
    }
    
    /**
     * 添加cookie 
     * @param name cookie名称
     * @param value cookie值
     * @param maxAge cookie存活时间
     * @param req 
     * @param response
     */
    public static void addCookie(String name,String value,int maxAge,HttpServletRequest req,HttpServletResponse response)
    {
    	addCookie(name, value, maxAge, req.getContextPath(), req, response);
    }
    
    /** 
     * 添加cookie
     * @param name cookie名称
     * @param value cookie值
     * @param maxAge cookie存货时间
     * @param path cookie路径
     * @param req
     * @param response
     */
    public static void addCookie(String name,String value,int maxAge, String path,HttpServletRequest req,HttpServletResponse response){
    	if(response==null){
    		return;
    	}
        Cookie cookie=new Cookie(name,value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    
    public static void addCookieAndSession(String name,String value,int maxAge, String path,HttpServletRequest req,HttpServletResponse response){
    	if(req == null){
    		return;
    	}
    	// 将属性加入session方便服务端获取
    	req.getSession().setAttribute(name, value);
    	// 将属性加入cookie，方便客户端获取
    	addCookie(name, value, maxAge, path, req, response);
    }
    
    /**
     * 删除cookie
     * @param name
     * @param context
     */
    public static void delCookie(String name,PageContext context)
    {
    	HttpServletResponse response=(HttpServletResponse)context.getResponse();
    	HttpServletRequest request=(HttpServletRequest)context.getRequest();
    	delCookie(name,request, response);
    }
    /**
     * 删除cookie
     * @param name
     * @param response
     */
    public static void delCookie(String name,HttpServletRequest request, HttpServletResponse response)
    {
    	addCookie(name, "", 0, request,response);
    }
    
    public static void delCookie(String name, String path, HttpServletRequest request, HttpServletResponse response){
    	addCookie(name, "", 0, path, request, response);
    }
    
    public static void delCookieAndSession(String name, String path, HttpServletRequest request, HttpServletResponse response){
    	if(request == null){
    		return;
    	}
    	// 删除session中的属性
    	request.getSession().removeAttribute(name);
    	// 删除cookie中的属性
    	addCookieAndSession(name, "", 0, path, request, response);
    }

    /**
     * 根据Cookie名取得Cookie的值.
      * 如果cookie 为空 则返回 null;
     * @param name String
     * @param context PageContext
     * @return String
     */
    public static String getValueByName(String name,PageContext context)
    {
        HttpServletRequest request=(HttpServletRequest)context.getRequest();
        String str= getValueByName(name,request);
        return str;
    }
    
    /**
     * 根据cookie名称取得值
     * @param name
     * @param request
     * @return
     */
    public static String getValueByName(String name,HttpServletRequest request)
    {
    	if(request==null)return "";
        Cookie cookies[]=request.getCookies();
        Cookie sCookie=null;
        String svalue=null;
        String sname=null;

        if(cookies==null)
            return null;
        for(int i=0;i< cookies.length;i++)
        {
            sCookie=cookies[i];
            sname=sCookie.getName();
            if(sname.equals(name))
            {
                svalue=sCookie.getValue();
                break;
            }
        }
        return svalue;
    }

    /**
     * 根据Cookie名判断Cookie是否存在.
     * @param name String
     * @param context PageContext
     * @return String
     */
    public static boolean isExistByName(String name,PageContext context)
    {
        HttpServletRequest request=(HttpServletRequest)context.getRequest();
        return isExistByName(name,request);
    }
    
    /**
     * 
     * @param name
     * @param request
     * @return
     */
    public static boolean isExistByName(String name,HttpServletRequest request)
    {
       
        Cookie cookies[]=request.getCookies();
        Cookie sCookie=null;
   
        String sname=null;
        boolean isExist=false;
        if(cookies==null)
            return false;
        for(int i=0;i< cookies.length;i++)
        {
            sCookie=cookies[i];
            sname=sCookie.getName();
            if(sname.equals(name))
            {
                isExist=true;
                break;
            }
        }
        return isExist;
    }
    
    /** 
     * 获取企业的cookie名称
     * 
     * @return
     */
    public static String getSwitchEntCookieName(){
    	return getSwitchEntCookieName(ContextSupportUtil.getCurrentUserId());
    }

    /** 
     * 获取企业的cookie名称
     * @param userId
     * @return
     */
    public static String getSwitchEntCookieName(Long userId){
    	if(userId == null){
    		return "";
    	}
    	return "ue_" + userId;
    }

    /**
     * 获取当前企业编码
     * @return
     */
    public static String getCurrentEnterpriseCode(){
        return getCurrentEnterpriseCode(ContextSupportUtil.getCurrentUserId());
    }
    
    public static String getCurrentEnterpriseCode(Long userId,HttpServletRequest request){
        if (request==null){
            ServletRequestAttributes requestAttribute = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if(requestAttribute == null){
                return null;
            }
            request= requestAttribute.getRequest();
        }
        return (String)request.getSession().getAttribute(getSwitchEntCookieName(userId));
    }

    public static String getCurrentEnterpriseCode(Long userId){
        return getCurrentEnterpriseCode(userId, null);
    }

    public static String getCurrentEnterpriseCode(HttpServletRequest request){
        return getCurrentEnterpriseCode(ContextSupportUtil.getCurrentUserId(), request);
    }
}
