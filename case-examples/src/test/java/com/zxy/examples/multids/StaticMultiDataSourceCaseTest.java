package com.zxy.examples.multids;

import com.zxy.examples.multids.config.StaticMultiDataSourceConfiguration;
import com.zxy.examples.multids.core.StaticRoutingDataSource;
import com.zxy.examples.multids.mapper.UserMapperExample;
import com.zxy.examples.multids.service.MultiDataSourceDemoService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticMultiDataSourceCaseTest {

    @Test
    public void testSwitchInServiceAndMapperLayer() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StaticMultiDataSourceConfiguration.class);
        try {
            MultiDataSourceDemoService service = context.getBean(MultiDataSourceDemoService.class);
            UserMapperExample mapper = context.getBean(UserMapperExample.class);
            StaticRoutingDataSource routingDataSource = context.getBean(StaticRoutingDataSource.class);

            assertEquals("mysql", service.readDefault());
            assertEquals("mysql2", service.readMysql2());
            assertEquals("pg>oracle>pg", service.serviceToMapperFlow(100L));
            assertEquals("mysql1", mapper.selectByName("demo"));
            assertEquals("mysql", routingDataSource.currentLookupKey());
        } finally {
            context.close();
        }
    }
}

