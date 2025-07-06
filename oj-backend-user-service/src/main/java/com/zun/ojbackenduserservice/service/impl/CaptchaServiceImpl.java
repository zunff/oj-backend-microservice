package com.zun.ojbackenduserservice.service.impl;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.constant.RedisConstant;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendcommon.utils.RedisUtils;
import com.zun.ojbackendcommon.utils.VerifyCodeUtil;
import com.zun.ojbackenduserservice.properties.EmailProperties;
import com.zun.ojbackenduserservice.service.CaptchaService;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 验证码接口实现类
 *
 * @author zunf
 * @date 2024/6/26 14:05
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private EmailProperties emailProperties;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private TemplateEngine templateEngine;

    /**
     * 发送邮件验证码
     *
     * @param email 邮箱
     */
    @Override
    public void sendEmailCaptcha(String email) {
        // 验证邮件配置是否完整
        validateEmailProperties();

        // 验证邮箱格式
        if (!VerifyCodeUtil.checkEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }

        // 生成或获取验证码
        String captcha = getCaptcha(email);

        // 生成邮件内容
        String content = generateEmailContent(captcha);

        // 发送邮件
        List<String> list = Collections.singletonList(email);
        sendEmail(list, content);
    }

    /**
     * 判断邮件配置是否完整
     */
    private void validateEmailProperties() {
        if (emailProperties.getUser() == null || emailProperties.getPassword() == null || emailProperties.getFrom() == null || emailProperties.getHost() == null || emailProperties.getPort() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱配置不完整");
        }
    }

    /**
     * 获取验证码
     *
     * @param email 邮箱地址，用于生成和存储验证码。
     * @return {@link String} 返回生成的验证码。
     */
    private String getCaptcha(String email) {
        // 根据邮箱生成Redis键名
        String redisKey = RedisConstant.CAPTCHA_CODE + email;
        // 尝试从Redis获取现有的验证码
        Object oldCode = redisUtils.get(redisKey);
        if (oldCode == null) {
            // 如果验证码不存在，生成新的验证码
            String captcha = VerifyCodeUtil.generateVerifyCode();
            // 将新生成的验证码存储到Redis，并设置过期时间
            boolean saveResult = redisUtils.set(redisKey, captcha, emailProperties.getExpireTime());
            if (!saveResult) {
                // 如果存储失败，抛出异常
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "缓存验证码失败");
            }
            return captcha;
        } else {
            // 如果验证码存在，报错邮件已经发送到邮箱
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "邮件已经发送到邮箱");
        }
    }


    /**
     * 生成邮件内容
     *
     * @param captcha 验证码
     * @return {@link String } 邮件内容
     */
    private String generateEmailContent(String captcha) {
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(captcha.split("")));
        return templateEngine.process("EmailVerificationCode.html", context);
    }

    /**
     * 发送邮件
     *
     * @param list
     * @param content 邮件内容
     */
    private void sendEmail(List<String> list, String content) {
        MailAccount account = createMailAccount();
        try {
            Mail.create(account)
                    .setTos(list.toArray(new String[0]))
                    .setTitle("OJ在线判题系统--用户注册")
                    .setContent(content)
                    .setHtml(true)
                    .setUseGlobalSession(false)
                    .send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
        }
    }

    /**
     * 创建邮件账户
     *
     * @return {@link MailAccount } 邮件账户
     */
    private MailAccount createMailAccount() {
        MailAccount account = new MailAccount();
        account.setAuth(true);
        account.setHost(emailProperties.getHost());
        account.setPort(emailProperties.getPort());
        account.setFrom(emailProperties.getFrom());
        account.setUser(emailProperties.getUser());
        account.setPass(emailProperties.getPassword());
        account.setSslEnable(true);
        account.setStarttlsEnable(true);
        return account;
    }

}
 
