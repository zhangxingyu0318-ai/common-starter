package com.zxy.examples.concurrent.spring;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpringCompletableFutureCaseTest {

    @Test
    public void testSpringThreadPoolPlusCompletableFuture() {
        SpringCompletableFutureCase example = new SpringCompletableFutureCase();
        try {
            List<String> result = example.processBatch(Arrays.asList(1, 2, 3, 4));
            assertEquals(4, result.size());
            assertTrue(result.contains("spring-async-1"));
            assertTrue(result.contains("spring-async-4"));
        } finally {
            example.shutdown();
        }
    }
}

