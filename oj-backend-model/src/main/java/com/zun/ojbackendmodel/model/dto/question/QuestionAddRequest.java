package com.zun.ojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author ZunF
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 题目标签（json 数组）
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题策略
     */
    private String judgeStrategy;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}