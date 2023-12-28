package com.zun.ojbackendserviceclient.service;

import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
