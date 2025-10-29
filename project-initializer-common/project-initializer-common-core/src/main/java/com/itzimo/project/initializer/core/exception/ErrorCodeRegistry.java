package com.itzimo.project.initializer.core.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 错误代码注册管理器
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public final class ErrorCodeRegistry {
    private static final Map<String, IErrorCode> ERROR_CODE_MAP = new ConcurrentHashMap<>();

    public static void registerErrorCode(IErrorCode errorCode) {
        ERROR_CODE_MAP.put(errorCode.getCode(), errorCode);
    }

    public static IErrorCode getErrorCode(String code) {
        return ERROR_CODE_MAP.get(code);
    }

    public static boolean containsCode(String code) {
        return ERROR_CODE_MAP.containsKey(code);
    }
}
   