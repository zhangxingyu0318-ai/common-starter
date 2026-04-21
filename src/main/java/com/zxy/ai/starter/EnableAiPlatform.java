package com.zxy.ai.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用AI平台功能
 * 在Spring Boot应用主类上添加此注解即可启用完整的AI平台功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AiPlatformAutoConfiguration.class)
public @interface EnableAiPlatform {

    /**
     * 是否启用所有AI模块
     * @return 默认启用
     */
    boolean enableAll() default true;

    /**
     * 自定义模块扫描包路径
     * @return 默认扫描com.zxy.ai包
     */
    String[] basePackages() default {"com.zxy.ai"};
}


