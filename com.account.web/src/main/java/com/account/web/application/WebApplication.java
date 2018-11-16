package com.account.web.application;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.version.mq.conf.MqConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Import({MqConfiguration.class})
@SpringBootApplication
@EnableDubboConfiguration
@ComponentScan(basePackages = {"com.account","com.common.config","com.version"})
@ImportResource(locations={"classpath:spring-config.xml"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}