package com.zun.ojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 自主实现的远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {
    //定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://192.168.1.106:8090/java/native/acm";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String bodyStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StrUtil.isBlank(bodyStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
        }
        return JSONUtil.toBean(bodyStr, ExecuteCodeResponse.class);
    }
}
