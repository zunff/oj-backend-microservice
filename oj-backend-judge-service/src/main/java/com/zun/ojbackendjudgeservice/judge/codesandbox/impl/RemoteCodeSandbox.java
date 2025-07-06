package com.zun.ojbackendjudgeservice.judge.codesandbox.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendjudgeservice.dto.ExecuteCodeRequest;
import com.zun.ojbackendjudgeservice.dto.ExecuteCodeResponse;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 自主实现的远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

    private String url;

    public RemoteCodeSandbox(String url) {
        this.url = url;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        String json = JSONUtil.toJsonStr(executeCodeRequest);
        HttpResponse httpResponse = HttpUtil.createPost(url)
                .body(json)
                .execute();
        String bodyStr = httpResponse.body();
        if (StrUtil.isBlank(bodyStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
        }
        return JSONUtil.toBean(bodyStr, ExecuteCodeResponse.class);
    }
}
