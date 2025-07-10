package com.zun.ojbackendcommon.model.qo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author ZunF
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码（邮箱）
     */
    private String captcha;
}
