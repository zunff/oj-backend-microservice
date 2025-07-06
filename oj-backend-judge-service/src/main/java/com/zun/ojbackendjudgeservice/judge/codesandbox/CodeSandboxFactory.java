package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 工厂模式
 */
@Component
public class CodeSandboxFactory {


    @Value("${codesandbox.url}")
    private String remoteCodeSandboxUrl;

    public CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox(remoteCodeSandboxUrl);
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
