package com.suneee.datasource;

import com.suneee.eas.common.datasource.DynamicDataSourceHolder;
import com.suneee.eas.common.datasource.TargetDataSource;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class DataSourceAspect {
    private static final Logger log= LogManager.getLogger(DataSourceAspect.class);
    //切换放在service接口的方法上，所以这里要配置AOP切面的切入点
    @Pointcut("execution( * com.suneee..*.service..*.*(..))")
    public void dataSourcePointCut() {
    }

    @Before("dataSourcePointCut()")
    public void before(JoinPoint joinPoint) {
        Method m=null;
        try {
            m=getAopMethod(joinPoint);
            if (m != null && m.isAnnotationPresent(TargetDataSource.class)){
                TargetDataSource data = m.getAnnotation(TargetDataSource.class);
                String dataSourceName = data.value();
                DynamicDataSourceHolder.putDataSource(dataSourceName);
                log.info("数据库切源成功："+dataSourceName);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //执行完切面后，将线程共享中的数据源名称清空
    @After("dataSourcePointCut()")
    public void after(JoinPoint joinPoint) {
        Method m=null;
        try {
            m=getAopMethod(joinPoint);
            if (m != null && m.isAnnotationPresent(TargetDataSource.class)){
                DynamicDataSourceHolder.removeDataSource();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否需要切源
     * @param joinPoint
     * @return
     * @throws NoSuchMethodException
     */
    private Method getAopMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        Object target = joinPoint.getTarget();
        String method = joinPoint.getSignature().getName();
        Class<?>[] clazz = target.getClass().getInterfaces();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        Method m = null;
        m=target.getClass().getMethod(method,parameterTypes);
        if (m==null){
            m = clazz[0].getMethod(method, parameterTypes);
        }
        //如果方法上存在切换数据源的注解，则根据注解内容进行数据源切换
        if (m != null && m.isAnnotationPresent(TargetDataSource.class)) {
            return m;
        }
        return null;
    }
}