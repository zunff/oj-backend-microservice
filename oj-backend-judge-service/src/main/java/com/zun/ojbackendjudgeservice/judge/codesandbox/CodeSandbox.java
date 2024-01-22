package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojapiclientsdk.client.OjApiClient;
import com.zun.ojapiclientsdk.model.ExecuteCodeRequest;
import com.zun.ojapiclientsdk.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
