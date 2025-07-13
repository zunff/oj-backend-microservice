package cn.com.zunf.ojbackendaiservice.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HtmlCrawlerToolsTest {

    @Resource
    private HtmlCrawlerTools htmlCrawlerTools;

    @Test
    public void testCrawlHtml() {

        String html = htmlCrawlerTools.crawlHtml("https://leetcode.cn/problems/number-of-ways-to-wear-different-hats-to-each-other/solutions/?languageTags=cpp,c");
        assertNotNull(html);
        System.out.println("提取的数据" + html);
    }

}