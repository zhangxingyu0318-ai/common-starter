package com.example.aicore;

import com.zxy.core.CommonCoreAutoConfiguration;
import com.zxy.core.GlobalExceptionHandler;
import com.zxy.core.LoggingAspect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiCoreApplicationTests {

    @Test
    void contextLoads() {
        CommonCoreAutoConfiguration configuration = new CommonCoreAutoConfiguration();
        GlobalExceptionHandler globalExceptionHandler = configuration.globalExceptionHandler();
        LoggingAspect loggingAspect = configuration.loggingAspect();

        assertNotNull(globalExceptionHandler);
        assertNotNull(loggingAspect);
    }

}
