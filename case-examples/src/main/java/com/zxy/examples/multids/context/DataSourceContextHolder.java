package com.zxy.examples.multids.context;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 线程内数据源上下文。
 */
public final class DataSourceContextHolder {

    private static final ThreadLocal<Deque<String>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceContextHolder() {
    }

    public static void push(String key) {
        CONTEXT.get().push(key);
    }

    public static void pop() {
        Deque<String> deque = CONTEXT.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
        if (deque.isEmpty()) {
            CONTEXT.remove();
        }
    }

    public static String peek() {
        Deque<String> deque = CONTEXT.get();
        return deque.isEmpty() ? null : deque.peek();
    }
}

