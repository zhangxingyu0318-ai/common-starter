package com.zxy.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 通过@RestControllerAdvice提供统一的异常处理
 *
 * 由CommonCoreAutoConfiguration自动装配，无需手动配置
 * 可通过配置禁用：zxy.exception-handling.enabled=false
 *
 * @author system
 * @create 2026/4/15
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return Result.error("参数错误: " + e.getMessage());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(SecurityException.class)
    public Result handleSecurityException(SecurityException e) {
        log.warn("Security exception: {}", e.getMessage());
        return Result.error("权限不足: " + e.getMessage());
    }

    /**
     * 处理IO异常
     */
    @ExceptionHandler(java.io.IOException.class)
    public Result handleIOException(java.io.IOException e) {
        log.error("IO exception", e);
        return Result.error("网络请求失败，请检查网络连接");
    }

    /**
     * 处理数据库异常（通过类名识别，避免直接依赖Spring-data-jpa）
     */
    @ExceptionHandler(Exception.class)
    public Result handleDatabaseException(Exception e) {
        String exceptionName = e.getClass().getName();
        if (exceptionName.contains("DataAccessException") ||
            exceptionName.contains("SQLException")) {
            log.error("Database exception", e);
            return Result.error("数据库操作失败，请稍后重试");
        }
        // 其他Exception异常也作兜底处理
        log.error("Exception caught", e);
        return Result.error("系统异常，请联系管理员");
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception", e);
        return Result.error("系统运行异常，请联系管理员");
    }

    /**
     * 处理未知异常（最后的兜底）
     */
    @ExceptionHandler(Throwable.class)
    public Result handleThrowable(Throwable e) {
        log.error("Unknown exception", e);
        return Result.error("系统异常，请联系管理员");
    }
}
