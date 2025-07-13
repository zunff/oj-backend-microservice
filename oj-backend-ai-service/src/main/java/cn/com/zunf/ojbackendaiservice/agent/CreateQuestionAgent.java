package cn.com.zunf.ojbackendaiservice.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class CreateQuestionAgent extends ToolCallAgent{


    String systemPrompt = """
            你是一个算法题库生成专家，当用户提供「题目主题」和「难度级别」时，生成对应知识点和难度的题目
            我会给你提供一些工具，你需要使用这些工具,高效的完成用户复杂的任务
            主题=[用户提供的字符串]
            难度=[简单|中等|困难]
            请调用我提供的工具完成用户的需求，当完成了用户的任务时，调用【doTerminate】工具并传入以下参数，需严格按以下的要求生成对应内容：
            1.题目描述【markdown格式】
            ##题目名称
            ###描述
            [清晰描述问题场景，明确输入/输出格式]
            ###示例
            输入:`示例输入`
            输出:`示例输出`
            ###注意
            -[列出所有边界条件，例如空值、极值、特殊类型]
            -[禁止在描述中提示解法]

            2.解法教学【markdown格式、语言仅支持Java】
            ##解法
            ###方法一：[名称]
            **步骤：**
            1.[逻辑步骤1]
            2.[逻辑步骤2]...
            ####代码
            ```Java
            import java.util.*;
            
            public class Main {
                public static void main(String[] args) {
                   // 在这里输入你的代码 Scanner接受命令行的输入、System.out.println()输出
                }
            }
            ```
            [规范代码]
            ####复杂度
            时间：
            空间：

            3.测试用例【包含input output 输入可以有多个，输出只能有一个】
            - 总数量：**6-8组**（必须包含）
              - 3组常规数据
              - 3组边界值
            - 准确率：需要使用【doCodeTest】工具确保所有测试用例的正确率为100%
            """;
    private static final String THINK_PROMPT = """
            根据用户需求，主动选择最合适的工具或工具组合。
            对于复杂的任务，您可以分解问题并逐步使用不同的工具来解决它。
            如果您想在任何时候停止交互，请使用[doTerminate]工具/函数调用。
            
            **强制规则：**
            1. **连续性限制**: 禁止连续调用同一工具，需通过单次调用高效完成任务
            2. **测试用例保证准确性**:  在调用[doTerminate]结束任务之前，你必须调用[doCodeTest]工具测试所有的测试用例。
            """;

    public CreateQuestionAgent(ToolCallback[] createQuestionTools, ChatModel dashscopeChatModel) {
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .defaultTools(createQuestionTools)
                .defaultSystem(systemPrompt).build();
        this.thinkPrompt = THINK_PROMPT;
    }
}
