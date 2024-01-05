package com.zun.ojbackendjudgeservice.judge.strategy.impl;


import cn.hutool.core.util.StrUtil;
import com.zun.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.zun.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.zun.ojbackendmodel.model.dto.question.JudgeConfig;
import com.zun.ojbackendmodel.model.enums.ExecuteCodeStatusEnum;
import com.zun.ojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * 执行结果与答案完全相等的判题策略
 * @author zunf
 */
public class SameJudgeStrategy implements JudgeStrategy {
    @Override
    public void judge(List<String> inputList, List<String> outputList, List<String> exampleOutputList, JudgeInfo judgeInfo) {
        for (int i = 0; i < outputList.size(); i++) {
            String output = outputList.get(i);
            String expect = exampleOutputList.get(i);
            if (!StrUtil.equals(output, expect)) {
                StringBuilder sb = new StringBuilder();
                sb.append(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                sb.append(", 执行输入用例：").append(inputList.get(i)).append(" 时出错");
                sb.append(", expect：").append(expect);
                sb.append(", but：").append(output);
                judgeInfo.setMessage(sb.toString());
                return;
            }
        }
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
    }
}
