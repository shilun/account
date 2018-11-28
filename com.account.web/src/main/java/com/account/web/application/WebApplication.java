package com.account.web.application;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.version.mq.conf.MqConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({MqConfiguration.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubboConfiguration
@ComponentScan(basePackages = {"com.account","com.common.config","com.version"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}