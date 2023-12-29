package com.zun.ojbackendquestionservice.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;

import com.zun.ojbackendcommon.annotation.AuthCheck;
import com.zun.ojbackendcommon.common.BaseResponse;
import com.zun.ojbackendcommon.common.DeleteRequest;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.common.ResultUtils;
import com.zun.ojbackendcommon.constant.UserConstant;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendcommon.exception.ThrowUtils;
import com.zun.ojbackendmodel.model.dto.question.*;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zun.ojbackendmodel.model.entity.Question;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendmodel.model.enums.JudgeStrategyEnum;
import com.zun.ojbackendmodel.model.vo.QuestionSubmitVO;
import com.zun.ojbackendmodel.model.vo.QuestionVO;
import com.zun.ojbackendquestionservice.service.QuestionService;
import com.zun.ojbackendquestionservice.service.QuestionSubmitService;
import com.zun.ojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 题目接口
 *
 * @author ZunF
 */
@RestController
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        List<JudgeCase> judgeCaseList = questionAddRequest.getJudgeCase();
        if (judgeCaseList != null) {
            String judgeStrategy = questionAddRequest.getJudgeStrategy();
            Pattern inputPattern = Pattern.compile("[^\\s]+(\\s[^\\s]+)*");
            Pattern outputPattern = Pattern.compile(".*;.*");

            for (JudgeCase judgeCase : judgeCaseList) {
                //判断输入案例是否合法
                Matcher inputMatcher = inputPattern.matcher(judgeCase.getInput());
                ThrowUtils.throwIf(!inputMatcher.matches(), ErrorCode.PARAMS_ERROR, "输入用例格式出错，格式：3 4，注意用例之间的空格");
                if (StrUtil.equals(judgeStrategy, JudgeStrategyEnum.ANY.getValue())) {
                    //如果判题策略是范围判题，那么需要判断每一个JudgeCase的输出用例是否合法，格式：3 4
                    Matcher outputMatcher = outputPattern.matcher(judgeCase.getOutput());
                    ThrowUtils.throwIf(!outputMatcher.matches(), ErrorCode.PARAMS_ERROR, "输出用例格式出错，格式：3;4，注意答案之间的英文分号");
                }
            }
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCaseList));
        }

        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        List<JudgeCase> judgeCaseList = questionUpdateRequest.getJudgeCase();
        if (judgeCaseList != null) {
            String judgeStrategy = questionUpdateRequest.getJudgeStrategy();
            Pattern inputPattern = Pattern.compile("[^\\s]+(\\s[^\\s]+)*");
            Pattern outputPattern = Pattern.compile(".*;.*");

            for (JudgeCase judgeCase : judgeCaseList) {
                //判断输入案例是否合法
                Matcher inputMatcher = inputPattern.matcher(judgeCase.getInput());
                ThrowUtils.throwIf(!inputMatcher.matches(), ErrorCode.PARAMS_ERROR, "输入用例格式出错，格式：3 4，注意用例之间的空格");
                if (StrUtil.equals(judgeStrategy, JudgeStrategyEnum.ANY.getValue())) {
                    //如果判题策略是范围判题，那么需要判断每一个JudgeCase的输出用例是否合法，格式：3 4
                    Matcher outputMatcher = outputPattern.matcher(judgeCase.getOutput());
                    ThrowUtils.throwIf(!outputMatcher.matches(), ErrorCode.PARAMS_ERROR, "输出用例格式出错，格式：3;4，注意答案之间的英文分号");
                }
            }
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCaseList));
        }
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取列表
     *
     * @param questionQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 本次提交变化数
     */
    @PostMapping("/submit/do")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                       HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userFeignClient.getLoginUser(request);
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取提交信息（仅管理员与用户本人可以看到提交信息中的提交的代码）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/submit/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionQueryRequest,
                                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionPage, userFeignClient.getLoginUser(request), request));
    }



}
