package com.zun.ojbackendcommon.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证码工具类
 *
 * @author zunf
 * @date 2024/6/26 13:58
 */
public class VerifyCodeUtil {
    //邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
    //验证码的字符集
    private static final String CODES = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //验证码的长度，可以根据需求进行修改
    private static final int CODE_LENGTH = 6;

    /**
     * 生成验证码
     *
     * @return {@link String }
     */
    public static String generateVerifyCode() {
        Random random = new Random();
        StringBuilder verifyCode = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            verifyCode.append(CODES.charAt(random.nextInt(CODES.length())));
        }
        return verifyCode.toString();
    }

    /**
     * 检测邮箱是否合法
     *
     * @param email 用户名
     * @return 合法状态
     */
    public static boolean checkEmail(String email) {
        // 检查输入是否为空或null
        if (email == null || email.isEmpty()) {
            return false;
        }
        // 使用正则表达式进行匹配
        Matcher m = EMAIL_PATTERN.matcher(email);
        return m.matches();
    }

}
