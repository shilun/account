package com.account.web.application;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.version.mq.conf.MqConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({MqConfiguration.class})
@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class})
@EnableDubboConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.account","com.version", "com.common.config"})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}