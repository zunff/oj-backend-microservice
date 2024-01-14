package com.zun.ojbackendcommon.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZunF
 * 消息队列配置类
 */
@Configuration
public class RabbitmqConfig {

    /**
     * 代码提交交换机
     */
    public static final String CODE_SUBMIT_EXCHANGE = "code.submit.exchange";

    /**
     * 代码提交队列
     */
    public static final String CODE_SUBMIT_QUEUE = "code.submit.queue";

    /**
     * 死信交换机
     */
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    /**
     * 代码提交死信队列
     */
    public static final String CODE_SUBMIT_DEAD_LETTER_QUEUE = "code.submit.dead.letter.queue";
    /**
     * 死信队列路由键a
     */
    public static final String CODE_SUBMIT_DEAD_LETTER_ROUTING_KEY = "code.submit.dead.letter";

    /**
     * 申明代码提交交换机
     * @return
     */
    @Bean
    public FanoutExchange codeSubmitExchange(){
        return new FanoutExchange(CODE_SUBMIT_EXCHANGE);
    }

    /**
     * 申明死信交换机
     * @return
     */
    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     * 申明代码提交队列
     * @return
     */
    @Bean
    public Queue codeSubmitQueue(){
        Map<String,Object> map = new HashMap<>();
        //绑定死信交换机，这个键是官方指定的
        map.put("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE);
        //绑定的死信路由键，这个键是官方指定的
        map.put("x-dead-letter-routing-key",CODE_SUBMIT_DEAD_LETTER_ROUTING_KEY);
        return QueueBuilder.durable(CODE_SUBMIT_QUEUE).withArguments(map).build();
    }

    /**
     * 申请代码提交死信队列
     * @return
     */
    @Bean
    public Queue codeSubmitDeadLetterQueue() {
        return new Queue(CODE_SUBMIT_DEAD_LETTER_QUEUE);
    }

    /**
     * 代码提交业务：队列绑定到交换机
     * @return
     */
    @Bean
    public Binding codeSubmitBinding(){
        return BindingBuilder.bind(codeSubmitQueue()).to(codeSubmitExchange());
    }

    /**
     * 代码提交死信队列绑定到死信交换机
     * @return
     */
    @Bean
    public Binding codeSubmitDeadLetterBinding(){
        return BindingBuilder.bind(codeSubmitDeadLetterQueue()).to(deadLetterExchange()).with(CODE_SUBMIT_DEAD_LETTER_ROUTING_KEY);
    }
}
