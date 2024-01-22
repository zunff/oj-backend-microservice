package com.zun.ojbackendjudgeservice.judge.strategy.manager;


import com.zun.ojapiclientsdk.model.JudgeInfo;
import com.zun.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.zun.ojbackendjudgeservice.judge.strategy.impl.SameJudgeStrategy;
import com.zun.ojbackendjudgeservice.judge.strategy.impl.AnyJudgeStrategy;
import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;


public class JudgeStrategyManager {

    public static JudgeInfo doJudge(JudgeContext judgeContext, String strategy) {
        JudgeStrategy judgeStrategy = null;
        switch (strategy) {
            case "same":
                judgeStrategy = new SameJudgeStrategy();
                break;
            case "any":
                judgeStrategy = new AnyJudgeStrategy();
                break;
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
