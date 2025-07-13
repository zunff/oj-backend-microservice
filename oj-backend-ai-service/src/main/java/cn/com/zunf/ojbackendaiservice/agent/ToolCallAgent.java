package cn.com.zunf.ojbackendaiservice.agent;

import cn.com.zunf.ojbackendaiservice.enums.AgentStatus;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ToolCallAgent extends ReActAgent{

    private final ToolCallingManager toolCallingManager;

    private final ChatOptions chatOptions;

    protected String thinkPrompt;

    public ToolCallAgent() {
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 自己实现代理工具调用
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true).build();
    }

    @Override
    public ChatResponse think(String userPrompt, String conversationId) {
        UserMessage userMessage = new UserMessage(userPrompt + "\n以上是用户的需求\n"  + thinkPrompt);
        List<Message> messages = new ArrayList<>(chatMemory.get(conversationId, toolMemorySize));
        messages.add(userMessage);
        chatMemory.add(conversationId, userMessage);
        Prompt prompt = new Prompt(messages, chatOptions);
        ChatResponse thinkChatResponse = chatClient.prompt(prompt).call().chatResponse();
        AssistantMessage assistantMessage = thinkChatResponse.getResult().getOutput();
        chatMemory.add(conversationId, assistantMessage);
        return thinkChatResponse;
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act(ChatResponse thinkChatResponse, String conversationId) {
        if (!thinkChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(chatMemory.get(conversationId, 10), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, thinkChatResponse);
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        chatMemory.add(conversationId, toolResponseMessage);
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> terminateToolName.equals(response.name()));
        if (terminateToolCalled) {
            statusThreadLocal.set(AgentStatus.SUCCESS);
        }
        return results;
    }

}
