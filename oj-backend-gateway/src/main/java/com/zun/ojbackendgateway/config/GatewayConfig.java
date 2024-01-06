package com.zun.ojbackendgateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.zun.ojbackendgateway.handler.SentinelFallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * 网关限流配置
 */
@Configuration
public class GatewayConfig
{
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler()
    {
        return new SentinelFallbackHandler();
    }

    @PostConstruct
    public void doInit() {
        // 加载网关限流规则
        initGatewayRules();
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        //构造器里写的是微服务的gateway.routes中的id
        rules.add(new GatewayFlowRule("oj-backend-question-service")
                .setCount(200)    // 限流阈值
                .setIntervalSec(1));   // 统计时间窗口，单位是秒，默认是 1 秒
        rules.add(new GatewayFlowRule("oj-backend-user-service")
                .setCount(100)
                .setIntervalSec(1));
        // 加载网关限流规则
        GatewayRuleManager.loadRules(rules);
    }
}