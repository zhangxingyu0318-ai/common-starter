package com.zxy.examples.multids.aspect;

import com.zxy.examples.multids.annotation.DataSourceKey;
import com.zxy.examples.multids.context.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 数据源切换切面。
 */
@Aspect
public class DataSourceSwitchAspect {

    @Around("execution(* com.zxy.examples..service..*(..)) || execution(* com.zxy.examples..mapper..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        DataSourceKey annotation = resolveDataSourceKey(joinPoint);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        DataSourceContextHolder.push(annotation.value());
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.pop();
        }
    }

    private DataSourceKey resolveDataSourceKey(ProceedingJoinPoint joinPoint) {
        Method method = resolveMethod(joinPoint);
        DataSourceKey annotation = findAnnotation(method, DataSourceKey.class);
        if (annotation != null) {
            return annotation;
        }

        Class<?> targetClass = joinPoint.getTarget().getClass();
        annotation = findAnnotation(targetClass, DataSourceKey.class);
        if (annotation != null) {
            return annotation;
        }

        return findOnInterfaces(targetClass, method, DataSourceKey.class);
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        Method signatureMethod = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        try {
            return joinPoint.getTarget().getClass().getMethod(signatureMethod.getName(), signatureMethod.getParameterTypes());
        } catch (Exception ignored) {
            return signatureMethod;
        }
    }

    private <A extends Annotation> A findOnInterfaces(Class<?> targetClass, Method method, Class<A> annotationType) {
        for (Class<?> iface : targetClass.getInterfaces()) {
            try {
                Method interfaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                A methodAnnotation = findAnnotation(interfaceMethod, annotationType);
                if (methodAnnotation != null) {
                    return methodAnnotation;
                }
            } catch (Exception ignored) {
                // ignore and continue
            }

            A classAnnotation = findAnnotation(iface, annotationType);
            if (classAnnotation != null) {
                return classAnnotation;
            }
        }
        return null;
    }

    private <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        return method == null ? null : method.getAnnotation(annotationType);
    }

    private <A extends Annotation> A findAnnotation(Class<?> type, Class<A> annotationType) {
        return type == null ? null : type.getAnnotation(annotationType);
    }
}


