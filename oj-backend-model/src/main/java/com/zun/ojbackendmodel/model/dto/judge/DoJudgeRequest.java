package com.zun.ojbackendmodel.model.dto.judge;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoJudgeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目提交编号
     */
    Long questionSubmitId;

    /**
     * 判题策略.传入null时默认为default
     */
    String judgeStrategy;
}
