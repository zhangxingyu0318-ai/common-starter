package com.zxy.examples.multids.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明方法/类使用的数据源 key。
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceKey {

    /**
     * 数据源标识，例如 mysql、mysql1、pg、oracle。
     */
    String value();
}

