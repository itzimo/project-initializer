package com.itzimo.project.initializer.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础异常类
 * 所有自定义异常类的父类
 *
 * @author chenmiao
 * @date 2025/10/26
 */
@Getter
@Setter
public class BaseException extends RuntimeException {
    /**
     * 错误码信息
     */
    private final IErrorCode errorCode;

    /**
     * 自定义错误消息（可选，覆盖默认消息）
     */
    private final String customMessage;

    public BaseException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    public BaseException(IErrorCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public BaseException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    public BaseException(IErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public String getErrorMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }

    public boolean hasCustomMessage() {
        return customMessage != null;
    }
}
