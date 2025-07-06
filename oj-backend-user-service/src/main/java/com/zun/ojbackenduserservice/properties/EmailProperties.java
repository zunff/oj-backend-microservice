package com.zun.ojbackenduserservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码邮箱配置参数
 *
 * @author zunf
 * @date 2024/6/26 13:54
 */
@Component
@ConfigurationProperties(prefix = "captcha.email")
@Data
public class EmailProperties {
    /**
     * 邮箱地址（注意：如果使用foxmail邮箱，此处user为qq号）
     */
    private String user;
    /**
     * 发件人昵称（必须正确，否则发送失败）
     */
    private String from;
    /**
     * 邮件服务器的SMTP地址
     */
    private String host;

    /**
     * 邮件服务器的SMTP端口
     */
    private Integer port;

    /**
     * 密码（授权码）
     */
    private String password;

    /**
     * 验证码过期时间
     */
    private Integer expireTime;
}
