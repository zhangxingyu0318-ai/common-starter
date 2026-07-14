package com.zxy.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器.
 * 通过@RestControllerAdvice提供统一的异常处理。
 * 由CommonCoreAutoConfiguration自动装配，无需手动配置。
 * 可通过配置禁用：zxy.exception-handling.enabled=false。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.fail(e.getErrorCode() == null ? "400" : e.getErrorCode(), e.getMessage(), e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return Result.fail("400", "参数错误: " + e.getMessage(), "Invalid argument: " + e.getMessage());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(SecurityException.class)
    public Result<Void> handleSecurityException(SecurityException e) {
        log.warn("Security exception: {}", e.getMessage());
        return Result.fail("403", "权限不足: " + e.getMessage(), "Forbidden: " + e.getMessage());
    }

    /**
     * 处理IO异常
     */
    @ExceptionHandler(java.io.IOException.class)
    public Result<Void> handleIOException(java.io.IOException e) {
        log.error("IO exception", e);
        return Result.fail("500", "网络请求失败，请检查网络连接", "Network request failed");
    }

    /**
     * 处理数据库异常（通过类名识别，避免直接依赖Spring-data-jpa）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleDatabaseException(Exception e) {
        String exceptionName = e.getClass().getName();
        if (exceptionName.contains("DataAccessException") ||
            exceptionName.contains("SQLException")) {
            log.error("Database exception", e);
            return Result.fail("500", "数据库操作失败，请稍后重试", "Database operation failed");
        }
        // 其他Exception异常也作兜底处理
        log.error("Exception caught", e);
        return Result.fail("500", "系统异常，请联系管理员", "System exception");
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception", e);
        return Result.fail("500", "系统运行异常，请联系管理员", "Runtime exception");
    }

    /**
     * 处理未知异常（最后的兜底）
     */
    @ExceptionHandler(Throwable.class)
    public Result<Void> handleThrowable(Throwable e) {
        log.error("Unknown exception", e);
        return Result.fail("500", "系统异常，请联系管理员", "Unexpected system exception");
    }
}
