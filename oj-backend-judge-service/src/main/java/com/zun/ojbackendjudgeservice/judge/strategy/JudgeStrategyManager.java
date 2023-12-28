package com.zun.ojbackendjudgeservice.judge.strategy;


import com.zun.ojbackendjudgeservice.judge.strategy.impl.DefaultJudgeStrategy;
import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;
import com.zun.ojbackendmodel.model.codesandbox.JudgeInfo;

public class JudgeStrategyManager {

    public static JudgeInfo doJudge(JudgeContext judgeContext, String strategy) {
        JudgeStrategy judgeStrategy = null;
        switch (strategy) {
            default:
                judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
