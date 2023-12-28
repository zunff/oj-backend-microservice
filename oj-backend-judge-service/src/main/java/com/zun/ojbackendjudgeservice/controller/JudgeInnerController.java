package com.zun.ojbackendjudgeservice.controller;

import com.zun.ojbackendjudgeservice.judge.JudgeService;
import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import com.zun.ojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param doJudgeRequest
     */
    @Override
    @PostMapping("/do")
    public void doJudge(@RequestBody DoJudgeRequest doJudgeRequest) {
        judgeService.doJudge(doJudgeRequest);
    }
}
