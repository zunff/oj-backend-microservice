package com.zun.ojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeCase implements Serializable {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;

    private static final long serialVersionUID = 1L;
}
