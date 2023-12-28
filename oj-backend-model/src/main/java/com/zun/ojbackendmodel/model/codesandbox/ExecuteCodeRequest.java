package com.zun.ojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 输入用例
     */
    private List<String> inputList;

    /**
     * 需要执行的代码
     */
    private String code;

    /**
     * 语言
     */
    private String language;
}
