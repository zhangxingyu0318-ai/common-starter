package com.zxy.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Common Core功能注解
 * 用于显式启用common-core模块的功能
 *
 * 使用方式：
 * @Configuration
 * @EnableCommonCore
 * public class YourAppConfig {
 * }
 *
 * @author system
 * @create 2026/4/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CommonCoreAutoConfiguration.class)
public @interface EnableCommonCore {
}
