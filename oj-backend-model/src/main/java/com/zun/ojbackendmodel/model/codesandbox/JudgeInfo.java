package com.zun.ojbackendmodel.model.codesandbox;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeInfo implements Serializable {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private long memory;

    /**
     * 消耗时间
     */
    private long time;

    private static final long serialVersionUID = 1L;
}
