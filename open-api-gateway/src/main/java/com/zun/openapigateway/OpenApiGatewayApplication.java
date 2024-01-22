package com.zun.openapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan("com.zun")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zun.ojbackendserviceclient.service"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true) //开启AOP
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OpenApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiGatewayApplication.class, args);
    }

}
