package cn.com.zunf.ojbackendaiservice.tools;

import cn.com.zunf.ojbackendaiservice.dto.JudgeCase;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 停止工具
 *
 * @author zunf
 * @date 2025/7/12 22:05
 */
@Slf4j
public class TerminateTools {
  
    @Tool(name = "doTerminate",
            description = "Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  \n" +
                    "When you have finished all the tasks, call this tool to end the work. ")
    public String doTerminate(@ToolParam(description = "题目描述MarkDown") String questionDescription,
                              @ToolParam(description = "解法教学MarkDown") String answer,
                              @ToolParam(description = "测试用例")List<JudgeCase> judgeCaseList
                              ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("description", questionDescription);
        jsonObject.set("answer", answer);
        jsonObject.set("judgeCaseList", judgeCaseList);
        return jsonObject.toString();
    }  
}
