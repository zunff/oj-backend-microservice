package com.zun.ojbackendjudgeservice.judge;

import com.zun.ojbackendcommon.model.qo.judge.DoJudgeRequest;

public interface JudgeService {
    /**
     * 判题
     * @param doJudgeRequest
     */
    void doJudge(DoJudgeRequest doJudgeRequest);
}
