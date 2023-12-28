package com.zun.ojbackendjudgeservice.manager;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.zun.ojbackendjudgeservice.judge.JudgeService;
import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows //运行时异常不需要try-catch，骗过javac编译，出现异常会直接抛出
    @RabbitListener(bindings = {
            //使用这个注解就是为了能够让他自动创建队列和交换机，并建立连接，也可以先创建好，直接指定从哪个队列读取
            @QueueBinding(
                    value = @Queue("code_queue"),
                    exchange = @Exchange(value = "code_exchange", type = "fanout")
            )
    }, ackMode = "MANUAL") //设置为手动应答
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
        log.info("receiveMessage：{}", message);
        DoJudgeRequest doJudgeRequest = JSONUtil.toBean(message, DoJudgeRequest.class);
        try {
            judgeService.doJudge(doJudgeRequest);
            channel.basicAck(deliverTag, false);
        } catch (Exception e) {
            //最后一个参数为是否重新塞回队列中，一般来说都不要塞回去，而是存到一个新的队列中
            channel.basicNack(deliverTag, false, false);
        }
    }

}
