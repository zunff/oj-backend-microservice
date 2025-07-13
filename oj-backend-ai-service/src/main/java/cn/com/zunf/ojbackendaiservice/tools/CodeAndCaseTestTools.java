package cn.com.zunf.ojbackendaiservice.tools;

import cn.com.zunf.ojbackendaiservice.enums.ExecuteCodeStatusEum;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.*;

/**
 * 测试代码用例工具
 *
 * @author zunf
 * @date 2025/7/12 22:05
 */
@NoArgsConstructor
@AllArgsConstructor
public class CodeAndCaseTestTools {

    private String apiUrl;
    @Tool(name = "doCodeTest", description = "Execute the example code and enter the input list, and return the output of the code, One call to the tool can test multiple cases")
    public String executeCode(
            @ToolParam(description = "Code to execute") String code,
            @ToolParam(description = "language: java") String language,
            @ToolParam(description = "Multiple parameters must to be separated by Spaces, cannot appear other characters, ps:['1 2 3', '3 4 5']") List<String> inputList) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", code);
        requestBody.put("language", language);
        requestBody.put("inputList", inputList);

        try (HttpResponse response = HttpRequest.post(apiUrl).body(JSONUtil.toJsonStr(requestBody))
                .timeout(10000).execute()){
            // 处理响应
            if (response.getStatus() == 200) {
                return parseResponse(JSONUtil.parseObj(response.body()));
            } else {
                return "error to execute by code:" + response.getStatus();
            }
        }
    }

    private String parseResponse(JSONObject response) {
        return response.getStr("outputList", "[]");
    }
}
