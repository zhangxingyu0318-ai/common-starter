package com.zxy.examples.concurrent.jdk21;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JDK21 多线程案例。
 *
 * <p>在 JDK21 上优先使用虚拟线程执行器；
 * 若当前运行时不支持（例如 Java8），自动降级为固定线程池。</p>
 */
public class Jdk21ConcurrencyCase {

    private static final int JDK21_FEATURE = 21;

    public ExecutionSummary processBatch(List<Integer> input) {
        if (input == null || input.isEmpty()) {
            return new ExecutionSummary(Collections.emptyList(), "none");
        }

        ExecutorSelection selection = selectExecutor();
        List<CompletableFuture<String>> futures = new ArrayList<CompletableFuture<String>>(input.size());
        for (Integer value : input) {
            futures.add(CompletableFuture.supplyAsync(() -> "jdk21-task-" + value, selection.executor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<String> result = new ArrayList<String>(futures.size());
        for (CompletableFuture<String> future : futures) {
            result.add(future.join());
        }

        if (selection.executorService != null) {
            selection.executorService.shutdown();
        }
        return new ExecutionSummary(result, selection.mode);
    }

    private ExecutorSelection selectExecutor() {
        if (isVirtualThreadSupported()) {
            ExecutorService virtualExecutor = createVirtualThreadExecutor();
            if (virtualExecutor == null) {
                throw new IllegalStateException("JDK21+ 环境下创建虚拟线程执行器失败");
            }
            return new ExecutorSelection(virtualExecutor, virtualExecutor, "virtual-thread");
        }

        ExecutorService fallback = Executors.newFixedThreadPool(4);
        return new ExecutorSelection(fallback, fallback, "platform-thread");
    }

    public static boolean isVirtualThreadSupported() {
        return detectFeatureVersion() >= JDK21_FEATURE
                && hasMethod(Executors.class, "newVirtualThreadPerTaskExecutor");
    }

    private static int detectFeatureVersion() {
        try {
            Method versionMethod = Runtime.class.getMethod("version");
            Object runtimeVersion = versionMethod.invoke(Runtime.getRuntime());
            Method featureMethod = runtimeVersion.getClass().getMethod("feature");
            return ((Integer) featureMethod.invoke(runtimeVersion)).intValue();
        } catch (Exception ignored) {
            return 8;
        }
    }

    private static boolean hasMethod(Class<?> targetClass, String methodName) {
        try {
            targetClass.getMethod(methodName);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static ExecutorService createVirtualThreadExecutor() {
        try {
            Method method = Executors.class.getMethod("newVirtualThreadPerTaskExecutor");
            return (ExecutorService) method.invoke(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class ExecutorSelection {
        private final Executor executor;
        private final ExecutorService executorService;
        private final String mode;

        private ExecutorSelection(Executor executor, ExecutorService executorService, String mode) {
            this.executor = executor;
            this.executorService = executorService;
            this.mode = mode;
        }
    }

    public static class ExecutionSummary {
        private final List<String> result;
        private final String mode;

        public ExecutionSummary(List<String> result, String mode) {
            this.result = result;
            this.mode = mode;
        }

        public List<String> getResult() {
            return result;
        }

        public String getMode() {
            return mode;
        }
    }
}

