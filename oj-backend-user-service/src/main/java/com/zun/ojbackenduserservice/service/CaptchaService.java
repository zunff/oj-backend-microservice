package com.zun.ojbackenduserservice.service;

/**
 * 验证码服务
 *
 * @author zunf
 * @date 2024/6/26 14:05
 */
public interface CaptchaService {
     /**
      * 发送验证码
      * @param email 邮箱
      */
     void sendEmailCaptcha(String email);
 }
 
