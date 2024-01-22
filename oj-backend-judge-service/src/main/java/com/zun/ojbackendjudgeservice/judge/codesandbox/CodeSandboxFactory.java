package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojapiclientsdk.client.OjApiClient;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 工厂模式
 */
@Component
public class CodeSandboxFactory {

    @Resource
    OjApiClient ojApiClient;

    public CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox(ojApiClient);
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
