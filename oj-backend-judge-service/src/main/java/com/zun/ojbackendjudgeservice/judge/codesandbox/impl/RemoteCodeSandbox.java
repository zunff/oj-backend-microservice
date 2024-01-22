package com.zun.ojbackendjudgeservice.judge.codesandbox.impl;

import com.zun.ojapiclientsdk.client.OjApiClient;
import com.zun.ojapiclientsdk.model.ExecuteCodeRequest;
import com.zun.ojapiclientsdk.model.ExecuteCodeResponse;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 自主实现的远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

    private OjApiClient ojApiClient;

    public RemoteCodeSandbox(OjApiClient ojApiClient) {
        this.ojApiClient = ojApiClient;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //调用自己封装的sdk
        return ojApiClient.execCodeAcmPattern(executeCodeRequest);


//        String url = "http://localhost:9000/open/exec/java/native/acm";
//        String json = JSONUtil.toJsonStr(executeCodeRequest);
//        HttpResponse httpResponse = HttpUtil.createPost(url)
////                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
//                .body(json)
//                .execute();
//        String bodyStr = httpResponse.body();
//        if (StrUtil.isBlank(bodyStr)) {
//            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
//        }
//        return JSONUtil.toBean(bodyStr, ExecuteCodeResponse.class);
    }
}
