package cn.com.zunf.ojbackendaiservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 智能体状态枚举
 *
 * @author zunf
 * @date 2025/7/12 21:00
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AgentStatus {

    /**
     * 1:空闲、2:运行中、3:运行成功、4:运行失败
     */
    IDLE("空闲", 1),
    RUNNING("运行中", 2),
    SUCCESS("运行成功", 3),
    FAIL("运行失败", 4);

    private String text;

    private Integer value;
}
