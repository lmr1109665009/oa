package com.suneee.eas.common.handler;

import com.suneee.eas.common.component.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static Logger log= LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * 表单验证拦截器
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value=BindException.class)
    public ResponseMessage bindExceptionHandler(HttpServletRequest request, HttpServletResponse response, BindException e){
        List<ObjectError> errorList=e.getAllErrors();
        return ResponseMessage.fail(errorList.get(0).getDefaultMessage());
    }

    /**
     * 所有异常报错
     * @param request
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value=Exception.class)
    public ResponseMessage allExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){
        e.printStackTrace();
        Enumeration<String> paraNames=request.getParameterNames();
        List<String> params=new ArrayList<>();
        for(;paraNames.hasMoreElements();){
            String key=paraNames.nextElement();
            String val=request.getParameter(key);
            params.add(key+"="+val);
        }
        log.error("请求服务异常："+request.getRequestURL()+",参数："+StringUtils.join(params,","),e);
        if ("bpmxDesignError".equals(e.getMessage())){
            return ResponseMessage.fail("请完善流程设计!");
        }
        return ResponseMessage.fail("服务器异常，请联系管理员！errorCode:"+e.getMessage());
    }  

}