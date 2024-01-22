package com.zun.ojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.zun.ojapiclientsdk.model.ExecuteCodeRequest;
import com.zun.ojapiclientsdk.model.ExecuteCodeResponse;
import com.zun.ojapiclientsdk.model.JudgeInfo;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendcommon.exception.ThrowUtils;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zun.ojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.zun.ojbackendjudgeservice.judge.strategy.manager.JudgeStrategyManager;
import com.zun.ojbackendjudgeservice.judge.strategy.model.JudgeContext;

import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import com.zun.ojbackendmodel.model.dto.question.JudgeCase;
import com.zun.ojbackendmodel.model.dto.question.JudgeConfig;
import com.zun.ojbackendmodel.model.entity.Question;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.zun.ojbackendmodel.model.enums.JudgeStrategyEnum;
import com.zun.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.zun.ojbackendserviceclient.service.QuestionFeignClient;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {


    @Value("${codesandbox.type:example}")//从配置文件中获取，默认为example
    private String type;

    @Resource
    private QuestionFeignClient questionService;

    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    @Override
    @GlobalTransactional
    public void doJudge(DoJudgeRequest doJudgeRequest) {
        Long questionSubmitId = doJudgeRequest.getQuestionSubmitId();
        String judgeStrategy = doJudgeRequest.getJudgeStrategy();
        //判断数据是否合法-->执行代码-->判断用例匹配情况-->更新数据库提交信息
        QuestionSubmit questionSubmit = questionService.getSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Long questionId = questionSubmit.getQuestionId();
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> exampleOutputList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        //执行代码
        //先更新QuestionSubmit数据库执行状态为执行中
        QuestionSubmit updateQuestionSubmit = new QuestionSubmit();
        updateQuestionSubmit.setId(questionSubmit.getId());
        updateQuestionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isSuccess = questionService.updateSubmitById(updateQuestionSubmit);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态错误");
        }
        //调用type指定的代码沙箱执行代码
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance(type);
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputList);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        //判断用例匹配情况，使用策略模式，可以在形参传入策略，根据策略名称获取不同的策略
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setExecuteCodeResponse(executeCodeResponse);
        judgeContext.setExampleOutputList(exampleOutputList);
        judgeContext.setJudgeConfig(judgeConfig);
        judgeContext.setInputList(inputList);
        judgeStrategy = judgeStrategy == null ? JudgeStrategyEnum.SAME.getValue() : judgeStrategy;
        JudgeInfo judgeInfo = JudgeStrategyManager.doJudge(judgeContext, judgeStrategy);

        //更新QuestionSubmit数据库执行状态
        updateQuestionSubmit = new QuestionSubmit();
        updateQuestionSubmit.setId(questionSubmit.getId());
        updateQuestionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            //如果Acc, 增加题目Acc次数
            isSuccess = questionService.addQuestionAccNum(questionId);
            ThrowUtils.throwIf(!isSuccess, ErrorCode.SYSTEM_ERROR, "更新通过数失败");
            updateQuestionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        } else {
            updateQuestionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        }
        isSuccess = questionService.updateSubmitById(updateQuestionSubmit);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态错误");
        }
    }
}
