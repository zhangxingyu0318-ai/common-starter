package com.zxy.ai.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Common Platform Auto Configuration
 * 自动配置当前可用的公共基础模块。
 */
@AutoConfiguration
@ComponentScan(basePackages = {
    "com.zxy.core"
})
public class AiPlatformAutoConfiguration {

    /**
     * Common Platform Starter
     * 引入此配置可获得当前公共基础功能。
     *
     * 包含功能：
     * - 通用工具 (ai-common-core)
     */
}


