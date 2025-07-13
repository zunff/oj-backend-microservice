package cn.com.zunf.ojbackendaiservice.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CodeAndCaseTestToolsTest {

    @Resource
    private CodeAndCaseTestTools codeAndCaseTestTools;

    @Test
    public void testExecuteCode() {
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}";
        String language = "java";
        String input = "1 2";
        String output = codeAndCaseTestTools.executeCode(code, language, List.of(input));
        System.out.println( output);
    }
}