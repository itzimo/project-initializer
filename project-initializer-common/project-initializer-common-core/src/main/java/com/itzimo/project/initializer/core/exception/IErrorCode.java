package com.itzimo.project.initializer.core.exception;

import com.itzimo.project.initializer.core.dto.ApiResponse;

/**
 * 接口错误码
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public interface IErrorCode {
    /**
     * 获取错误码
     *
     * @return {@link String }
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return {@link String }
     */
    String getMessage();

    // 错误码判断
    default boolean isError() {
        return !isSuccess();
    }

    // 判断是否为成功
    default boolean isSuccess() {
        return "00000".equals(getCode());
    }

    // 判断是否为用户端错误
    default boolean isClientError() {
        return getCode().startsWith("A");
    }

    // 判断是否为系统错误
    default boolean isSystemError() {
        return getCode().startsWith("B");
    }

    // 判断是否为第三方错误
    default boolean isExternalError() {
        return getCode().startsWith("C");
    }

    default BusinessException asException() {
        return new BusinessException(this);
    }

    default BusinessException asException(String customMessage) {
        return new BusinessException(this, customMessage);
    }

    default BusinessException asException(Throwable cause) {
        return new BusinessException(this, cause);
    }

    default <T> ApiResponse<T> toResponse() {
        return ApiResponse.fail(this);
    }

    default <T> ApiResponse<T> toResponse(String customMessage) {
        return ApiResponse.fail(this, customMessage);
    }

    default <T> ApiResponse<T> toResponse(T data) {
        return ApiResponse.data(this, data);
    }

    default void register() {
        ErrorCodeRegistry.registerErrorCode(this);
    }
}
