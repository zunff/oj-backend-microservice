package com.zun.ojbackendjudgeservice.manager;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.zun.ojbackendcommon.config.RabbitmqConfig;
import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.zun.ojbackendserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import org.springframework.amqp.core.Message;

import javax.annotation.Resource;


@Component
@Slf4j
public class DeadLetterReceiver {

    @Resource
    private QuestionFeignClient questionService;

    /**
     * 监听代码提交死信队列
     */
    @RabbitListener(queues = RabbitmqConfig.CODE_SUBMIT_DEAD_LETTER_QUEUE)
    public void queuea(Message msg, Channel channel) throws IOException {
        String message = new String(msg.getBody());
        log.info("代码提交死信队列收到死信【{}】",message);
        //修改题目提交状态为失败
        DoJudgeRequest doJudgeRequest = JSONUtil.toBean(message, DoJudgeRequest.class);
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setId(doJudgeRequest.getQuestionSubmitId());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        questionService.updateSubmitById(questionSubmit);
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        log.info("死信消息properties：{}", msg.getMessageProperties());
    }
}
