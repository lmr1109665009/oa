package com.suneee.eas.oa.handler;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.oa.exception.common.InvalidParamterException;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @user 子华
 * @created 2018/9/27
 */
@ControllerAdvice
@ResponseBody
public class CarExceptionHandler {

    /**
     * 流程异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(FlowableException.class)
    public ResponseMessage activitiExceptionHandle(FlowableException e) {
        e.printStackTrace();
        Throwable cause = e.getCause();
        String message="";
        if (cause!=null){
            message=cause.getMessage();
        }
        if (StringUtil.isEmpty(message)){
            message=e.getMessage();
        }
        return ResponseMessage.fail(message);
    }

    /**
     * 无效参数处理
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidParamterException.class)
    public ResponseMessage invalidParamterExceptionHandle(InvalidParamterException e) {
        e.printStackTrace();
        return ResponseMessage.fail(e.getMessage());
    }
}
