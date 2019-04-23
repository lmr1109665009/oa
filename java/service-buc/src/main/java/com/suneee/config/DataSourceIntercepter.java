package com.suneee.config;

import com.suneee.eas.common.datasource.DynamicDataSourceHolder;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据源切换拦截器
 * @user 子华
 * @created 2018/9/28
 */
public class DataSourceIntercepter implements HandlerInterceptor {
    private static Logger log= LogManager.getLogger(DataSourceIntercepter.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String env= ContextUtil.getRuntimeEnv();
        if (StringUtil.isNotEmpty(env)){
            DynamicDataSourceHolder.putDataSource(env+"-master");
            log.info("数据库切源成功："+env+"-master");
        }
        log.info("当前数据源："+ DynamicDataSourceHolder.getDataSource()+"，当前运行环境是："+env);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        DynamicDataSourceHolder.removeDataSource();
    }
}
