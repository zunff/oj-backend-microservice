package com.zun.ojbackendjudgeservice.judge.strategy.model;


import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.zun.ojbackendmodel.model.dto.question.JudgeConfig;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {

    /**
     * 沙箱执行代码的结果
     */
    private ExecuteCodeResponse executeCodeResponse;

    /**i
     * 测试的的输入用例
     */
    private List<String> inputList;

    /**
     * 规定的输出用例
     */
    private List<String> exampleOutputList;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;
}
