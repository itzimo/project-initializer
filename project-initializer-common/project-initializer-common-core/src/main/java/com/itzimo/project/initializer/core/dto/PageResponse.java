package com.itzimo.project.initializer.core.dto;

import com.itzimo.project.initializer.core.exception.IErrorCode;

import java.time.LocalDateTime;
import java.util.List;

public record PageResponse<T>(
        // 基本响应字段
        boolean success,
        String code,
        String message,
        // 分页数据
        List<T> data,
        // 分页信息
        long total,
        long current,
        long size,
        int pages,
        boolean hasNext,
        boolean hasPrevious,
        // 时间戳
        String timestamp
) {
    public static <T> PageResponse<T> page(IErrorCode error, List<T> records,
                                           long total, long current, long size) {
        int pages = (int) Math.ceil((double) total / size);
        return new PageResponse<>(
                error.isSuccess(),
                error.getCode(),
                error.getMessage(),
                records,
                total,
                current,
                size,
                pages,
                current < pages,
                current > 1,
                LocalDateTime.now().toString()
        );
    }

}
