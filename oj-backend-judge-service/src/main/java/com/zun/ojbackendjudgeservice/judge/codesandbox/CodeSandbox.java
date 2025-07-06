package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojbackendjudgeservice.dto.ExecuteCodeRequest;
import com.zun.ojbackendjudgeservice.dto.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
