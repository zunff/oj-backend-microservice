package com.zun.ojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeConfig implements Serializable {

    /**
     * 时间限制（ms）
     */
    private int timeLimit;

    /**
     * 内存限制（KB）
     */
    private int memoryLimit;

    /**
     * 堆栈限制（KB）
     */
    private int stackLimit;

    private static final long serialVersionUID = 1L;
}
