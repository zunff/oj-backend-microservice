package cn.com.zunf.ojbackendaiservice.controller;

import cn.com.zunf.ojbackendaiservice.agent.CreateQuestionAgent;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CompletableFuture;


/**
 * AI相关接口
 *
 * @author zunf
 * @date 2025/7/13 23:31
 */
@RestController
public class AiController {
    @Resource
    private CreateQuestionAgent createQuestionAgent;

    @GetMapping("/create/question")
    public SseEmitter createQuestion(@RequestParam String userPrompt, @RequestParam String difficulty) {
        SseEmitter sseEmitter = new SseEmitter(600000L);
        CompletableFuture.runAsync(() -> {
            createQuestionAgent.run(sseEmitter, difficulty + "难度，" + userPrompt);
        });
        return sseEmitter;
    }
}
