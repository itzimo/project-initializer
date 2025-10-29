package com.itzimo.project.initializer.core.exception;

/**
 * 业务异常
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public final class BusinessException extends BaseException {
    public BusinessException(IErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(IErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BusinessException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BusinessException(IErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}