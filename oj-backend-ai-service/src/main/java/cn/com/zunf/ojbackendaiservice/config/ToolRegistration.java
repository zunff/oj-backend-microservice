package cn.com.zunf.ojbackendaiservice.config;

import cn.com.zunf.ojbackendaiservice.tools.CodeAndCaseTestTools;
import cn.com.zunf.ojbackendaiservice.tools.HtmlCrawlerTools;
import cn.com.zunf.ojbackendaiservice.tools.TerminateTools;
import cn.com.zunf.ojbackendaiservice.tools.WebSearchTools;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册工具类
 *
 * @author zunf
 * @date 2025/7/12 22:02
 */
@Configuration
public class ToolRegistration {

    @Value("${codeSandBox.apiUrl}")
    private String codeSandBoxApiUrl;

    @Value("${baidu.apiKey}")
    private String baiduApiKey;

    private CodeAndCaseTestTools codeAndCaseTestTools;
    private HtmlCrawlerTools htmlCrawlerTools;
    private WebSearchTools webSearchTools;
    private TerminateTools terminateTools;

    @PostConstruct
    public void init() {
        // 在注入完成后初始化工具对象
        codeAndCaseTestTools = new CodeAndCaseTestTools(codeSandBoxApiUrl);
        htmlCrawlerTools = new HtmlCrawlerTools();
        webSearchTools = new WebSearchTools(baiduApiKey);
        terminateTools = new TerminateTools();
    }

    @Bean
    public ToolCallback[] createQuestionTools() {
        return ToolCallbacks.from(codeAndCaseTestTools, htmlCrawlerTools, webSearchTools, terminateTools);
    }
}
