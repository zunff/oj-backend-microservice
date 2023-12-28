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
public class ExecuteCodeResponse {
    /**
     * 程序执行结果
     */
    private List<String> outputList;

    /**
     * 执行状态（成功、失败）
     */
    private Integer status;

    /**
     * 执行信息（方法）
     */
    private String  message;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
