package com.suneee.eas.common.utils;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.HttpConstant;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * http请求工具类
 * @user 子华
 * @created 2018/9/12
 */
public class HttpUtil {
    /**
     * http请求响应数据
     * @param response
     * @param message
     * @param headers
     */
    public static void writeResponseJson(HttpServletResponse response, ResponseMessage message, Map<String,String> headers) throws IOException {
        response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
        if (headers!=null){
            for (String key:headers.keySet()){
                response.setHeader(key,headers.get(key));
            }
        }
        response.getWriter().println(JsonUtil.toJson(message));
    }
    public static void writeResponseJson(HttpServletResponse response, ResponseMessage message) throws IOException {
        writeResponseJson(response,message,null);
    }

    public static void writeResponseJson(HttpServletResponse response, String json, Map<String,String> headers) throws IOException {
        response.setContentType(HttpConstant.CONTENT_TYPE_JSON);
        if (headers!=null){
            for (String key:headers.keySet()){
                response.setHeader(key,headers.get(key));
            }
        }
        response.getWriter().println(json);
    }
    public static void writeResponseJson(HttpServletResponse response, String json) throws IOException {
        writeResponseJson(response,json,null);
    }
}
