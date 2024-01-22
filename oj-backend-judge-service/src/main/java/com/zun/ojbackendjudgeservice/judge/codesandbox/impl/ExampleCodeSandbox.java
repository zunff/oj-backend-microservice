package com.zun.ojbackendjudgeservice.judge.codesandbox.impl;



import com.zun.ojapiclientsdk.model.ExecuteCodeRequest;
import com.zun.ojapiclientsdk.model.ExecuteCodeResponse;
import com.zun.ojapiclientsdk.model.JudgeInfo;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zun.ojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.zun.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱（为了调通代码）
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = ExecuteCodeResponse.builder().build();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        executeCodeResponse.setMessage("测试执行成功");
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
