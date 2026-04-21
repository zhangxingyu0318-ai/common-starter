package com.zxy.examples.multids.mapper;

import com.zxy.examples.multids.annotation.DataSourceKey;

/**
 * 模拟 MyBatis Mapper 接口。
 */
public interface UserMapperExample {

    @DataSourceKey("oracle")
    String selectById(Long id);

    @DataSourceKey("mysql1")
    String selectByName(String name);
}

