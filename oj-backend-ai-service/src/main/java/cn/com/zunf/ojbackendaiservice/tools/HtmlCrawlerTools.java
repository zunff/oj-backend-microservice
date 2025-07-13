package cn.com.zunf.ojbackendaiservice.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 爬虫工具类
 *
 * @author zunf
 * @date 2025/7/12 22:05
 */
public class HtmlCrawlerTools {
    @Tool(name = "crawler", description = "Scrape the content of a web page")
    public String crawlHtml(
            @ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)...")
                    .timeout(10000)
                    .get();
            doc.select("link").remove();
            doc.select("script").remove();
            return  doc.html();
        } catch (IOException e) {
            return "Crawl failed: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Invalid URL: " + url;
        }
    }
}