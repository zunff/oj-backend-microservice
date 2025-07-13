package cn.com.zunf.ojbackendaiservice.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络搜索工具
 *
 * @author ZunF
 * @date 2023/10/31 15:01
 */
@NoArgsConstructor
public class WebSearchTools {

    private String apiKey;

    public WebSearchTools(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(name = "webSearch", description = "Search information from baidu search engine")
    public String webSearch(@ToolParam(description = "keyword to search") String keyword) {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", Collections.singletonList(
                Map.of("content", keyword, "role", "user")
        ));
        requestBody.put("search_filter", Map.of(
                "match", Map.of("site", Collections.singletonList("leetcode.cn"))
        ));

        // 发送API请求
        try (HttpResponse response = HttpRequest.post("https://qianfan.baidubce.com/v2/ai_search/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .timeout(5000)
                .execute()) {

            // 处理响应
            if (response.getStatus() == 200) {
                return parseResponse(JSONUtil.parseObj(response.body()));
            } else {
                return "error to search by code:" + response.getStatus();
            }
        }
    }

    private String parseResponse(Map<String, Object> response) {
        // 提取并格式化结果
        List<Map<String, Object>> references = (List<Map<String, Object>>) response.get("references");
        StringBuilder result = new StringBuilder();

        for (Map<String, Object> item : references) {
            result.append("title: ").append(item.get("title")).append("\n")
                    .append("content: ").append(item.get("content").toString(), 0, Math.min(66, item.get("content").toString().length())).append("\n")
                    .append("url: ").append(item.get("url")).append("\n\n");
        }
        return result.toString();
    }
}