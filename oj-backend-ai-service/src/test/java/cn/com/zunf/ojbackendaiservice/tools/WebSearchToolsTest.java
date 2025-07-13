package cn.com.zunf.ojbackendaiservice.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSearchToolsTest {

    @Resource
    private WebSearchTools webSearchTools;

    @Test
    public void testWebSearch() {
        String result = webSearchTools.webSearch("动态规划");
        assertNotNull(result);
        System.out.println(result);
    }

}