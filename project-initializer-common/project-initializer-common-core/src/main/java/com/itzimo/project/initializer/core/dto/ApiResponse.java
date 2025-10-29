package com.itzimo.project.initializer.core.dto;

import com.itzimo.project.initializer.core.enums.ErrorCode;
import com.itzimo.project.initializer.core.exception.IErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

public record ApiResponse<T>(
        // 是否成功
        boolean success,
        // 响应码，"00000"表示成功
        String code,
        // 响应消息
        String message,
        // 响应数据
        T data,
        // 时间戳
        String timestamp
) {
    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return data(ErrorCode.SUCCESS, data);
    }


    public static <T> ApiResponse<T> fail(IErrorCode error) {
        return data(error, null);
    }

    public static <T> ApiResponse<T> fail(IErrorCode error, String customMessage) {
        return new ApiResponse<>(
                error.isSuccess(),
                error.getCode(),
                customMessage != null ? customMessage : error.getMessage(),
                null,
                LocalDateTime.now().toString()
        );
    }

    public static <T> ApiResponse<T> data(IErrorCode error, T data) {
        return new ApiResponse<>(
                error.isSuccess(),
                error.getCode(),
                error.getMessage(),
                data,
                LocalDateTime.now().toString()
        );
    }

    public boolean isOk() {
        return this.success;
    }

    public boolean isFail() {
        return !this.success;
    }

    // 安全获取数据，避免NPE
    public Optional<T> getSafeData() {
        return Optional.ofNullable(this.data);
    }
}
