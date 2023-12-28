package com.zun.ojbackendjudgeservice.judge.strategy;


import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;
import com.zun.ojbackendmodel.model.codesandbox.JudgeInfo;

public interface JudgeStrategy {
    /**
     * 具体判题逻辑
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
