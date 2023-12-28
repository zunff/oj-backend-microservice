package com.zun.ojbackendjudgeservice.judge.codesandbox;


import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 静态工厂模式
 */
public class CodeSandboxFactory {

    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
