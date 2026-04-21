package com.zxy.examples.multids.core;

import com.zxy.examples.multids.context.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 静态路由数据源。
 */
public class StaticRoutingDataSource extends AbstractRoutingDataSource {

    private final String defaultKey;

    public StaticRoutingDataSource(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.peek();
    }

    /**
     * 仅用于案例与测试：返回当前生效的 key。
     */
    public String currentLookupKey() {
        String key = (String) determineCurrentLookupKey();
        return key == null ? defaultKey : key;
    }
}

