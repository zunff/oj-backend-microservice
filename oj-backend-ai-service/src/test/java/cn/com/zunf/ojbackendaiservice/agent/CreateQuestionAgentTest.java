package cn.com.zunf.ojbackendaiservice.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateQuestionAgentTest {

    @Resource
    private CreateQuestionAgent createQuestionAgent;

    @Test
    public void test() {
//        createQuestionAgent.run("难度【简单】，二分查找");
    }
}