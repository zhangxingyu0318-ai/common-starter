package com.zxy.examples.multids.service;

import com.zxy.examples.multids.annotation.DataSourceKey;
import com.zxy.examples.multids.core.StaticRoutingDataSource;
import com.zxy.examples.multids.mapper.UserMapperExample;

/**
 * Service 层数据源切换示例。
 */
public class MultiDataSourceDemoService {

    private final StaticRoutingDataSource routingDataSource;
    private final UserMapperExample userMapper;

    public MultiDataSourceDemoService(StaticRoutingDataSource routingDataSource, UserMapperExample userMapper) {
        this.routingDataSource = routingDataSource;
        this.userMapper = userMapper;
    }

    public String readDefault() {
        return routingDataSource.currentLookupKey();
    }

    @DataSourceKey("mysql2")
    public String readMysql2() {
        return routingDataSource.currentLookupKey();
    }

    @DataSourceKey("pg")
    public String serviceToMapperFlow(Long id) {
        String beforeMapper = routingDataSource.currentLookupKey();
        String mapperDataSource = userMapper.selectById(id);
        String afterMapper = routingDataSource.currentLookupKey();
        return beforeMapper + ">" + mapperDataSource + ">" + afterMapper;
    }
}

