package com.zxy.examples.concurrent.spring;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Spring 线程池 + CompletableFuture 示例。
 *
 * <p>这里不使用 @EnableAsync / @Async，避免影响全局 Spring 上下文。</p>
 */
public class SpringCompletableFutureCase {

    private final ThreadPoolTaskExecutor executor;

    public SpringCompletableFutureCase() {
        this.executor = createDefaultExecutor();
    }

    public SpringCompletableFutureCase(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    public List<String> processBatch(List<Integer> input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<String>> futures = new ArrayList<CompletableFuture<String>>(input.size());
        for (Integer value : input) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(
                    () -> "spring-async-" + value,
                    getExecutor()
            );
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<String> result = new ArrayList<String>(futures.size());
        for (CompletableFuture<String> future : futures) {
            result.add(future.join());
        }
        return result;
    }

    public void shutdown() {
        executor.shutdown();
    }

    protected Executor getExecutor() {
        return executor;
    }

    private static ThreadPoolTaskExecutor createDefaultExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("case-spring-async-");
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(5);
        taskExecutor.initialize();
        return taskExecutor;
    }
}

