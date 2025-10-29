package com.itzimo.project.initializer.core.exception;

import com.itzimo.project.initializer.core.dto.ApiResponse;
import com.itzimo.project.initializer.core.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理程序
 *
 * @author chenmiao
 * @date 2025/10/28
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e,
                                                     HttpServletRequest request) {
        // 记录业务异常日志
        if (e.getErrorCode().isSystemError()) {
            log.error("系统异常 [{}] {} - {}",
                    e.getCode(), e.getErrorMessage(), request.getRequestURI(), e);
        } else {
            log.warn("业务异常 [{}] {} - {}",
                    e.getCode(), e.getErrorMessage(), request.getRequestURI());
        }

        return ApiResponse.fail(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ErrorCode.USER_REQUEST_PARAMETER_ERROR.toResponse(errorMsg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknownException(Exception e,
                                                    HttpServletRequest request) {
        log.error("未知异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return ErrorCode.SYSTEM_EXECUTION_ERROR.toResponse("系统繁忙，请稍后重试");
    }
}