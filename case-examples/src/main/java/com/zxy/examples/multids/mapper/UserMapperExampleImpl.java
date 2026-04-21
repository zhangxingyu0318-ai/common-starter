package com.zxy.examples.multids.mapper;

import com.zxy.examples.multids.core.StaticRoutingDataSource;

/**
 * Mapper 示例实现类。
 */
public class UserMapperExampleImpl implements UserMapperExample {

    private final StaticRoutingDataSource routingDataSource;

    public UserMapperExampleImpl(StaticRoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    @Override
    public String selectById(Long id) {
        return routingDataSource.currentLookupKey();
    }

    @Override
    public String selectByName(String name) {
        return routingDataSource.currentLookupKey();
    }
}

