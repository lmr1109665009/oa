package com.suneee.eas.oa.exception.common;

/**
 * 无效参数异常
 * @user 子华
 * @created 2018/9/28
 */
public class InvalidParamterException extends Exception {
    public InvalidParamterException() {
    }

    public InvalidParamterException(String message) {
        super(message);
    }

    public InvalidParamterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParamterException(Throwable cause) {
        super(cause);
    }

    public InvalidParamterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
