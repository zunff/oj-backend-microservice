package cn.com.zunf.ojbackendaiservice.utils;

import cn.com.zunf.ojbackendaiservice.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public class SseEmitterUtil {

    public static void sendMessage(SseEmitter sseEmitter, MessageType messageType, String message) {
        try {
            sseEmitter.send(SseEmitter.event().name(messageType.getText()).data(message));
        } catch (Exception e) {
            log.error("sse send message fail: {}", e.getMessage());
        }
    }
}
