package com.suneee.eas.common.constant;

import com.suneee.eas.common.component.RestTemplateInterceptor;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @user 子华
 * @created 2018/7/20
 */
public class ServiceConstant {
    /**
     * 获取当前运行环境参数
     * @param runtimeEnv
     * @return
     */
    private static String getRuntimeEnvStr(String runtimeEnv){
        if (StringUtil.isEmpty(runtimeEnv)){
            return "";
        }
        return "-"+runtimeEnv.toUpperCase();
    }

    /**
     * 获取当前运行环境前缀
     * @param runtimeEnv
     * @return
     */
    private static String getRuntimeEnvPrefix(String runtimeEnv){
        if (StringUtil.isEmpty(runtimeEnv)){
            return "";
        }
        return runtimeEnv+"-";
    }
    private static String getHeaderRuntimeEnv(){
        HttpServletRequest request=ContextUtil.getRequest();
        if (request==null){
            return "";
        }
        if (request.getAttribute("runtimeEnv")!=null){
            return (String) request.getAttribute("runtimeEnv");
        }
        return request.getHeader("runtimeEnv");
    }

    /**
     * 设置restTemplate请求头参数
     * @param runtimeEnv
     */
    private static void setRestRequestHeader(String runtimeEnv){
        if (StringUtil.isEmpty(runtimeEnv)){
            return;
        }
        RestTemplateInterceptor.addHeader("runtimeEnv",runtimeEnv);
    }
    /**
     * 配置中心服务
     */
    public static String getConfigServiceUrl(String runtimeEnv){
        setRestRequestHeader(runtimeEnv);
        //配置中心暂时不启用多环境部署
        //return "http://CONFIG"+getRuntimeEnvStr(runtimeEnv)+"-SERVER";
        return "http://CONFIG-SERVER";
    }
    public static String getConfigServiceUrl(){
        return getConfigServiceUrl(getHeaderRuntimeEnv());
    }
    /**
     * OA服务
     */
    public static String getOaServiceUrl(String runtimeEnv){
        setRestRequestHeader(runtimeEnv);
        return "http://SERVICE"+getRuntimeEnvStr(runtimeEnv)+"-OA/"+getRuntimeEnvPrefix(runtimeEnv)+"oa";
    }
    public static String getOaServiceUrl(){
        return getOaServiceUrl(getHeaderRuntimeEnv());
    }
    /**
     * 路由网关服务
     */
    public static String getGatewayServiceUrl(String runtimeEnv){
        setRestRequestHeader(runtimeEnv);
        //网关暂时不启用多环境部署
        //return "http://SERVICE"+getRuntimeEnvStr(runtimeEnv)+"-GATEWAY";
        return "http://SERVICE-GATEWAY";
    }
    public static String getGatewayServiceUrl(){
        return getGatewayServiceUrl(getHeaderRuntimeEnv());
    }
    /**
     * 用户中心服务
     */
    public static String getBucServiceUrl(String runtimeEnv){
        setRestRequestHeader(runtimeEnv);
        //buc暂时不启用多环境部署
//        return "http://SERVICE"+getRuntimeEnvStr(runtimeEnv)+"-BUC/"+getRuntimeEnvPrefix(runtimeEnv)+"buc";
        return "http://SERVICE-BUC/buc";
    }
    public static String getBucServiceUrl(){
        return getBucServiceUrl(getHeaderRuntimeEnv());
    }
    /**
     * 流程引擎中心服务
     */
    public static String getBpmxServiceUrl(String runtimeEnv){
        setRestRequestHeader(runtimeEnv);
        //bpmx暂时不启用多环境部署
        //return "http://SERVICE"+getRuntimeEnvStr(runtimeEnv)+"-BPMX/"+getRuntimeEnvPrefix(runtimeEnv)+"bpmx";
        return "http://SERVICE-BPMX/bpmx";
    }
    public static String getBpmxServiceUrl(){
        return getBpmxServiceUrl(getHeaderRuntimeEnv());
    }
}
