package cn.com.zunf.ojbackendaiservice.agent;

import cn.com.zunf.ojbackendaiservice.enums.AgentStatus;
import cn.com.zunf.ojbackendaiservice.utils.SseEmitterUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static cn.com.zunf.ojbackendaiservice.enums.MessageType.*;

@Slf4j
public abstract class BaseAgent {

    protected String terminateToolName = "doTerminate";

    protected Integer maxStep = 10;

    protected Integer toolMemorySize = 16;

    protected ChatClient chatClient;

    protected ThreadLocal<AgentStatus> statusThreadLocal = ThreadLocal.withInitial(() -> AgentStatus.IDLE);

    protected final ChatMemory chatMemory = new InMemoryChatMemory();

    public void run(SseEmitter sseEmitter, String userPrompt) {
        String conversationId = Thread.currentThread().threadId() + "";
        if (statusThreadLocal.get() != AgentStatus.IDLE) {
            log.warn("Agent is not idle, please wait a moment");
            SseEmitterUtil.sendMessage(sseEmitter, ERROR, "正在为您生成中，请耐心等待");
            return;
        }
        if (StrUtil.isBlank(userPrompt)) {
            log.warn("User prompt cannot be empty");
            SseEmitterUtil.sendMessage(sseEmitter, ERROR, "请输入你的需求");
            return;
        }
        statusThreadLocal.set(AgentStatus.RUNNING);
        try {
            int i = 1;
            while (statusThreadLocal.get() == AgentStatus.RUNNING) {
                if (i >= maxStep) {
                    statusThreadLocal.set(AgentStatus.FAIL);
                    SseEmitterUtil.sendMessage(sseEmitter, ERROR, "生成题目失败，达到最大循环数量，请重试");
                    break;
                }
                try {
                    String stepResult = step(userPrompt, conversationId);
                    log.info("Step {}: {}", i, stepResult);
                    SseEmitterUtil.sendMessage(sseEmitter, TOOL, StrUtil.format("Step {}: {}", i, stepResult));
                } catch (Exception e) {
                    statusThreadLocal.set(AgentStatus.FAIL);
                    log.error("Step {} error: {}", i, e.getMessage());
                    SseEmitterUtil.sendMessage(sseEmitter, ERROR, StrUtil.format("Step {} error: {}", i, e.getMessage()));
                } finally {
                    i++;
                }
            }
        } finally {
            statusThreadLocal.remove();
            chatMemory.clear(conversationId);
            SseEmitterUtil.sendMessage(sseEmitter, ERROR, "已结束此次任务");
            sseEmitter.complete();
        }
    }

    public abstract String step(String userPrompt, String conversationId);
}
