package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.zun.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
