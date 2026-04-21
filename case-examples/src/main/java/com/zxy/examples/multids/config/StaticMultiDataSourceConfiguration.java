package com.zxy.examples.multids.config;

import com.zxy.examples.multids.aspect.DataSourceSwitchAspect;
import com.zxy.examples.multids.core.MockNamedDataSource;
import com.zxy.examples.multids.core.StaticRoutingDataSource;
import com.zxy.examples.multids.mapper.UserMapperExample;
import com.zxy.examples.multids.mapper.UserMapperExampleImpl;
import com.zxy.examples.multids.service.MultiDataSourceDemoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态多数据源配置示例。
 *
 * <p>该配置只在示例模块手动创建上下文时生效，不会影响业务模块默认上下文。</p>
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class StaticMultiDataSourceConfiguration {

    public static final String DEFAULT_DS = "mysql";

    @Bean
    public StaticRoutingDataSource routingDataSource() {
        StaticRoutingDataSource routingDataSource = new StaticRoutingDataSource(DEFAULT_DS);
        routingDataSource.setTargetDataSources(buildTargetDataSources());
        routingDataSource.setDefaultTargetDataSource(mysqlDataSource("demo_mysql"));
        routingDataSource.setLenientFallback(false);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    @Bean
    public DataSourceSwitchAspect dataSourceSwitchAspect() {
        return new DataSourceSwitchAspect();
    }

    @Bean
    public UserMapperExample userMapperExample(StaticRoutingDataSource routingDataSource) {
        return new UserMapperExampleImpl(routingDataSource);
    }

    @Bean
    public MultiDataSourceDemoService multiDataSourceDemoService(
            StaticRoutingDataSource routingDataSource,
            UserMapperExample userMapperExample
    ) {
        return new MultiDataSourceDemoService(routingDataSource, userMapperExample);
    }

    private Map<Object, Object> buildTargetDataSources() {
        Map<Object, Object> dataSources = new HashMap<Object, Object>();

        // 可以按需静态扩展更多数据源：mysql3、pg2、oracle2 ...
        dataSources.put("mysql", mysqlDataSource("demo_mysql"));
        dataSources.put("mysql1", mysqlDataSource("demo_mysql_1"));
        dataSources.put("mysql2", mysqlDataSource("demo_mysql_2"));
        dataSources.put("pg", pgDataSource("demo_pg"));
        dataSources.put("oracle", oracleDataSource("demo_oracle"));
        return dataSources;
    }

    private DataSource mysqlDataSource(String dbName) {
        return new MockNamedDataSource("mysql:" + dbName);
    }

    private DataSource pgDataSource(String dbName) {
        return new MockNamedDataSource("pg:" + dbName);
    }

    private DataSource oracleDataSource(String serviceName) {
        return new MockNamedDataSource("oracle:" + serviceName);
    }
}



