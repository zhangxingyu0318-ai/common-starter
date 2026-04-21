package com.zxy.examples.multids;

import com.zxy.examples.multids.config.StaticMultiDataSourceConfiguration;
import com.zxy.examples.multids.core.StaticRoutingDataSource;
import com.zxy.examples.multids.service.MultiDataSourceDemoService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 静态多数据源案例运行入口。
 */
public class MultiDataSourceCaseRunner {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StaticMultiDataSourceConfiguration.class);
        try {
            MultiDataSourceDemoService service = context.getBean(MultiDataSourceDemoService.class);
            StaticRoutingDataSource routingDataSource = context.getBean(StaticRoutingDataSource.class);

            System.out.println("default = " + service.readDefault());
            System.out.println("mysql2  = " + service.readMysql2());
            System.out.println("flow    = " + service.serviceToMapperFlow(1L));
            System.out.println("after   = " + routingDataSource.currentLookupKey());
        } finally {
            context.close();
        }
    }
}

