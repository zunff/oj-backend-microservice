package com.zun.ojbackendjudgeservice.manager;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.zun.ojbackendcommon.config.RabbitmqConfig;
import com.zun.ojbackendjudgeservice.judge.JudgeService;
import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageReceiver {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows //运行时异常不需要try-catch，骗过javac编译，出现异常会直接抛出
    @RabbitListener(queues = RabbitmqConfig.CODE_SUBMIT_QUEUE, ackMode = "MANUAL")
    public void codedSubmitConsumer(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
        log.info("receiveMessage：{}", message);
        DoJudgeRequest doJudgeRequest = JSONUtil.toBean(message, DoJudgeRequest.class);
        try {
            judgeService.doJudge(doJudgeRequest);
            //如果上面的代码卡死了，导致没有应答，那么交换机就不会把消息发过来了
            channel.basicAck(deliverTag, false);
        } catch (Exception e) {
            //最后一个参数为是否重新塞回队列中，一般来说都不要塞回去，而是存到一个新的队列中
            channel.basicNack(deliverTag, false, false);
        }
    }

}
