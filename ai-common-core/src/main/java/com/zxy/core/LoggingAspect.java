package com.zxy.core;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 日志记录切面
 * 自动记录所有@RestController和@Controller标注的类中public方法的：入参、出参、执行耗时
 * 用于审计和性能监控
 *
 * @author system
 * @create 2026/4/16
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * 定义切点：匹配所有@RestController标注的类中的public方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && execution(public * *(..))")
    public void restControllerPointcut() {
    }

    /**
     * 定义切点：匹配所有@Controller标注的类中的public方法
     */
    @Pointcut("@within(org.springframework.stereotype.Controller) && execution(public * *(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：记录方法执行的详细信息
     */
    @Around("restControllerPointcut() || controllerPointcut()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法信息
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 获取入参
        Object[] args = joinPoint.getArgs();
        String argsStr = formatArgs(args);

        // 记录方法开始执行
        log.info(">>> 【{}】方法执行开始 | 入参: {}", className + "." + methodName, argsStr);

        // 记录执行时间
        long startTime = System.currentTimeMillis();
        Object result;

        try {
            // 执行方法
            result = joinPoint.proceed();

            // 计算执行耗时
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录方法执行结束
            String resultStr = formatResult(result);
            log.info("<<< 【{}】方法执行成功 | 出参: {} | 耗时: {}ms",
                     className + "." + methodName, resultStr, duration);

            return result;

        } catch (Throwable e) {
            // 计算执行耗时
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录异常
            log.error("!!! 【{}】方法执行异常 | 异常: {} | 耗时: {}ms",
                      className + "." + methodName, e.getMessage(), duration, e);

            throw e;
        }
    }

    /**
     * 格式化入参
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "无";
        }

        try {
            return Arrays.stream(args)
                    .map(this::formatObject)
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            log.warn("Failed to format args", e);
            return "无法解析";
        }
    }

    /**
     * 格式化出参
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            return formatObject(result);
        } catch (Exception e) {
            log.warn("Failed to format result", e);
            return "无法解析";
        }
    }

    /**
     * 格式化单个对象，使用FastJSON2序列化
     */
    private String formatObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        try {
            // 对于字符串，直接限制长度
            if (obj instanceof String) {
                String str = (String) obj;
                return str.length() > 200 ? str.substring(0, 200) + "..." : str;
            }

            // 对于基本类型和包装类，直接转换
            if (isPrimitiveOrWrapper(obj.getClass())) {
                return obj.toString();
            }

            // 使用FastJSON2序列化对象，限制输出长度
            String jsonStr = JSON.toJSONString(obj);
            return jsonStr.length() > 200 ? jsonStr.substring(0, 200) + "..." : jsonStr;

        } catch (Exception e) {
            // FastJSON序列化失败，降级到toString()
            log.debug("FastJSON serialization failed, fallback to toString()", e);
            try {
                String str = obj.toString();
                return str.length() > 200 ? str.substring(0, 200) + "..." : str;
            } catch (Exception ex) {
                return "无法序列化";
            }
        }
    }

    /**
     * 判断是否是基本类型或包装类
     */
    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
               type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Boolean.class ||
               type == Byte.class ||
               type == Short.class ||
               type == Character.class;
    }
}
