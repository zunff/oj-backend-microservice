package cn.com.zunf.ojbackendaiservice.agent;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类  
 * 实现了思考-行动的循环模式  
 */
@Slf4j
public abstract class ReActAgent extends BaseAgent {  
  
    /**  
     * 处理当前状态并决定下一步行动  
     *
     * @param userPrompt  用户输入
     * @param conversationId 会话ID
     * @return 思考结果
     */  
    public abstract ChatResponse think(String userPrompt, String conversationId);
  
    /**  
     * 执行决定的行动  
     *
     * @param thinkChatResponse 思考结果
     * @param conversationId 会话ID
     * @return 行动执行结果  
     */  
    public abstract String act(ChatResponse thinkChatResponse, String conversationId);
  
    /**  
     * 执行单个步骤：思考和行动  
     *
     * @param userPrompt 用户输入
     * @param conversationId 会话ID
     * @return 步骤执行结果  
     */  
    @Override  
    public String step(String userPrompt, String conversationId) {
        try {
            ChatResponse chatResponse = think(userPrompt, conversationId);
            List<AssistantMessage.ToolCall> toolCallList = chatResponse.getResult().getOutput().getToolCalls();
            if (CollUtil.isEmpty(toolCallList)) {
                return "思考完成 - 无需行动";  
            }
            log.info("选择调用以下工具:{}", toolCallList.stream().map(toolCall -> StrUtil.format("\n工具：{} 参数：{}", toolCall.name(), toolCall.arguments())).collect(Collectors.joining()));
            return act(chatResponse, conversationId);
        } catch (Exception e) {
            return "步骤执行失败: " + e.getMessage();  
        }  
    }  
}
