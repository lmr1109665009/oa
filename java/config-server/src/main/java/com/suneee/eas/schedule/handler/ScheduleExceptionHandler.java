package com.suneee.eas.schedule.handler;

import com.suneee.eas.common.component.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class ScheduleExceptionHandler {
    private static Logger log= LogManager.getLogger(ScheduleExceptionHandler.class);

    /**
     * 定时任务已存在异常拦截器
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value= ObjectAlreadyExistsException.class)
    public ResponseMessage bindExceptionHandler(HttpServletRequest request, HttpServletResponse response, ObjectAlreadyExistsException e){
        return ResponseMessage.fail("定时任务已存在");
    }

    /**
     * 定时任务调度异常拦截器
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value= SchedulerException.class)
    public ResponseMessage bindExceptionHandler(HttpServletRequest request, HttpServletResponse response, SchedulerException e){
        return ResponseMessage.fail("定时任务执行异常："+e.getMessage());
    }

}