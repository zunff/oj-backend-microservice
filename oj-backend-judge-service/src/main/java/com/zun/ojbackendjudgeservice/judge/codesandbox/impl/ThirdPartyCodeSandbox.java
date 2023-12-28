package com.zun.ojbackendjudgeservice.judge.codesandbox.impl;


import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 调用第三方服务的代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = ExecuteCodeResponse.builder().build();
        System.out.println("第三方代码沙箱");
        return executeCodeResponse;
    }
}
