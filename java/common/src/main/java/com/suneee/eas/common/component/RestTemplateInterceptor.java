package com.suneee.eas.common.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * restTemplate拦截器
 *
 * @user 子华
 * @created 2018/8/16
 */
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private static ThreadLocal<Map<String, String>> headerThreadLocal = new ThreadLocal<>();

    /**
     * 添加请求头
     * @param key
     * @param val
     */
    public static void addHeader(String key, String val) {
        Map<String,String> body=headerThreadLocal.get();
        if (body==null){
            body=new HashMap<>();
            headerThreadLocal.set(body);
        }
        body.put(key,val);
    }

    /**
     * 清除请求头参数
     */
    public static void clearHeader(){
        headerThreadLocal.remove();
    }

    /**
     * 删除请求头参数
     * @param key
     */
    public static void removeHeader(String key){
        Map<String,String> body=headerThreadLocal.get();
        if (body==null){
            return;
        }
        body.remove(key);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest req = requestAttributes.getRequest();
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                String value = req.getHeader(key);
                headers.add(key, value);
            }
        }
        Map<String,String> headerMap=headerThreadLocal.get();
        if (headerMap!=null&&headerMap.size()>0){
            for (String key:headerMap.keySet()){
                headers.add(key, headerMap.get(key));
            }
            headerThreadLocal.remove();
        }
        // 保证请求继续被执行
        return execution.execute(request, body);
    }
}
