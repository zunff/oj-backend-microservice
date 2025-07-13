package cn.com.zunf.ojbackendaiservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 消息类型
 *
 * @author zunf
 * @date 2025/7/13 21:47
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MessageType {

    //
    ERROR("error"),
    TOOL("tool"),
    RESUlT("result");

    private String text;
}
