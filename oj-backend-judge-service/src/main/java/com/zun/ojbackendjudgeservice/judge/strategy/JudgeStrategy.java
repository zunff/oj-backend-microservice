package com.zun.ojbackendjudgeservice.judge.strategy;


import com.zun.ojapiclientsdk.model.ExecuteCodeResponse;
import com.zun.ojapiclientsdk.model.JudgeInfo;
import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;

import com.zun.ojbackendmodel.model.dto.question.JudgeConfig;
import com.zun.ojbackendmodel.model.enums.ExecuteCodeStatusEnum;
import com.zun.ojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;

public interface JudgeStrategy {
    /**
     * 具体判题逻辑
     * @param judgeContext
     * @return
     */
    default JudgeInfo doJudge(JudgeContext judgeContext) {
        ExecuteCodeResponse executeCodeResponse = judgeContext.getExecuteCodeResponse();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        //如果沙箱在执行代码时出现了错误，就直接返回该错误，无需再进行判断执行结果是否正确
        Integer status = executeCodeResponse.getStatus();
        if (!status.equals(ExecuteCodeStatusEnum.SUCCESS.getValue())) {
            if (judgeInfo == null) {
                judgeInfo = new JudgeInfo();
            }
            String message = executeCodeResponse.getMessage();
            judgeInfo.setMessage(ExecuteCodeStatusEnum.getEnumByValue(status).getText() + (message == null ? "" : ", " + message));
            return judgeInfo;
        }
        //接下来就是程序正常运行，返回了结果，先判断内存和时间是否符合
        long memory = judgeInfo.getMemory();
        long time = judgeInfo.getTime();
        JudgeConfig judgeConfig = judgeContext.getJudgeConfig();
        if (memory > judgeConfig.getMemoryLimit()) {
            judgeInfo.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfo;
        }
        if (time > judgeConfig.getTimeLimit()) {
            judgeInfo.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfo;
        }
        //判断程序输出是否与预期输出一致
        List<String> outputList = executeCodeResponse.getOutputList();
        List<String> exampleOutputList = judgeContext.getExampleOutputList();
        //判题后的信息会在传入的judgeInfo中修改
        judge(judgeContext.getInputList(), outputList, exampleOutputList, judgeInfo);
        return judgeInfo;
    }

    void judge(List<String> inputList, List<String> outputList, List<String> exampleOutputList, JudgeInfo judgeInfo);
}
