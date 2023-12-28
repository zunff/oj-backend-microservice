package com.zun.ojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan("com.zun")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.zun.ojbackendserviceclient.service"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true) //开启AOP
public class OjBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendUserServiceApplication.class, args);
    }

}
