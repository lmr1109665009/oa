package com.suneee.eas.oa.exception.common;

/**
 * @user 子华
 * @created 2018/9/8
 */
public class BpmnNotFoundException extends Exception {
    public BpmnNotFoundException() {
    }

    public BpmnNotFoundException(String message) {
        super(message);
    }

    public BpmnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpmnNotFoundException(Throwable cause) {
        super(cause);
    }

    public BpmnNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
