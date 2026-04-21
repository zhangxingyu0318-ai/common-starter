package com.zxy.ai.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Common Platform Auto Configuration
 * 自动配置所有通用模块和AI模块的功能
 */
@AutoConfiguration
@ComponentScan(basePackages = {
    "com.zxy.core",
    "com.zxy.ai.data",
    "com.zxy.ai.model",
    "com.zxy.ai.training"
})
public class AiPlatformAutoConfiguration {

    /**
     * Common Platform Starter
     * 引入此依赖即可获得完整的Spring Boot + AI平台功能
     *
     * 包含功能：
     * - 通用工具 (common-core)
     * - 数据处理 (ai-data)
     * - 模型管理 (ai-model)
     * - 训练功能 (ai-training)
     */
}


