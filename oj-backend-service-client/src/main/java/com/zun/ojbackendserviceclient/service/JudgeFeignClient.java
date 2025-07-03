package com.zun.ojbackendserviceclient.service;

import com.zun.ojbackendcommon.model.qo.judge.DoJudgeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ZunF
 */
@FeignClient(name = "oj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    /**
     * 判题
     * @param doJudgeRequest
     */
    @PostMapping("/do")
    void doJudge(@RequestBody DoJudgeRequest doJudgeRequest);
}
