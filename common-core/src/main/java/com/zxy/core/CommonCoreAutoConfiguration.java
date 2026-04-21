package com.zxy.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

/**
 * Common Core 自动配置类
 * 当common-core被引入到Spring Boot应用时，自动装配异常处理器
 *
 * @author system
 * @create 2026/4/15
 */
@AutoConfiguration
@ConditionalOnClass(RestController.class)
public class CommonCoreAutoConfiguration {

    /**
     * 自动装配全局异常处理器
     * 默认开启，可通过zxy.exception-handling.enabled=false关闭
     */
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    @ConditionalOnProperty(name = "zxy.exception-handling.enabled", havingValue = "true", matchIfMissing = true)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * 装配日志记录切面
     */
    @Bean
    @ConditionalOnMissingBean(LoggingAspect.class)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
