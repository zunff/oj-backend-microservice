package com.zun.ojbackenduserservice.controller;


import com.zun.ojbackendcommon.common.BaseResponse;
import com.zun.ojbackendcommon.common.ResultUtils;
import com.zun.ojbackenduserservice.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码接口
 *
 * @author zunf
 * @date 2024/6/26 14:03
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    @Resource
    private final CaptchaService captchaService;

    @GetMapping("/email-captcha")
    public BaseResponse<Boolean> sendEmailCaptcha(String email) {
        captchaService.sendEmailCaptcha(email);
        return ResultUtils.success(true);
    }

}
