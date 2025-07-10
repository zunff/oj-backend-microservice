package com.zun.ojbackendcommon.constant;

/**
 * Redis常量
 *
 * @author zunf
 * @date 2024/6/26 16:54
 */
public interface RedisConstant {

    /**
     * 验证码缓存
     */
    String CAPTCHA_CODE = "captcha-email:";
    String USER_LOGIN_KEY = "user-login:";
}
