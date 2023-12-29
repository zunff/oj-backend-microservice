package com.zun.ojbackendquestionservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.constant.CommonConstant;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendcommon.exception.ThrowUtils;
import com.zun.ojbackendcommon.utils.SqlUtils;
import com.zun.ojbackendmodel.model.dto.judge.DoJudgeRequest;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zun.ojbackendmodel.model.entity.Question;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.zun.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.zun.ojbackendmodel.model.vo.QuestionSubmitVO;
import com.zun.ojbackendmodel.model.vo.QuestionVO;
import com.zun.ojbackendquestionservice.manager.MessageProducer;
import com.zun.ojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.zun.ojbackendquestionservice.service.QuestionService;
import com.zun.ojbackendquestionservice.service.QuestionSubmitService;
import com.zun.ojbackendserviceclient.service.JudgeFeignClient;
import com.zun.ojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author ZunF
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-10-17 15:23:48
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

//    @Resource
//    @Lazy
//    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MessageProducer messageProducer;

    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //判断编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        ThrowUtils.throwIf(languageEnum == null, ErrorCode.PARAMS_ERROR, "暂不支持该编程语言");

        Long questionId = questionSubmitAddRequest.getQuestionId();
        String code = questionSubmitAddRequest.getCode();

        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 提交
        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(code);
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean isSuccess = save(questionSubmit);
        ThrowUtils.throwIf(!isSuccess, ErrorCode.SYSTEM_ERROR, "插入数据失败");

        //异步执行用户提交的代码，策略使用默认策略
//        CompletableFuture.runAsync(() -> {
//            DoJudgeRequest doJudgeRequest = new DoJudgeRequest();
//            doJudgeRequest.setQuestionSubmitId(questionSubmit.getId());
//            doJudgeRequest.setJudgeStrategy(null);
//            judgeFeignClient.doJudge(doJudgeRequest);
//        });
        DoJudgeRequest doJudgeRequest = new DoJudgeRequest();
        doJudgeRequest.setQuestionSubmitId(questionSubmit.getId());
        doJudgeRequest.setJudgeStrategy(question.getJudgeStrategy());
        String json = JSONUtil.toJsonStr(doJudgeRequest);
        //使用fanout，不需要routingKey
        messageProducer.sendMessage("code_exchange", "", json);

        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //关联查询用户信息
        Long userId = questionSubmit.getUserId();
        //脱敏：仅管理员与用户本人可以看到提交信息中的提交的代码和答案
        if (loginUser == null || userId != loginUser.getId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode("");
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        Set<Long> questionIdSet = questionSubmitList.stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserMap = userFeignClient.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Question>> questionIdQuestionMap = questionService.listByIds(questionIdSet).stream().collect(Collectors.groupingBy(Question::getId));
        List<QuestionSubmitVO> submitVOS = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(questionSubmit, loginUser);
            questionSubmitVO.setUserVO(userFeignClient.getUserVO(userIdUserMap.get(questionSubmit.getUserId()).get(0)));
            Question question = questionIdQuestionMap.get(questionSubmit.getQuestionId()).get(0);
            QuestionVO questionVO = questionService.getQuestionVO(question, request);
            questionSubmitVO.setQuestionVO(questionVO);
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(submitVOS);
        return questionSubmitVOPage;
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param questionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doQuestionSubmitInner(long userId, long questionId) {
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        QueryWrapper<QuestionSubmit> thumbQueryWrapper = new QueryWrapper<>(questionSubmit);
        QuestionSubmit oldQuestionSubmit = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已提交
        if (oldQuestionSubmit != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 提交数 - 1
                result = questionService.update()
                        .eq("id", questionId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未提交
            result = this.save(questionSubmit);
            if (result) {
                // 提交数 + 1
                result = questionService.update()
                        .eq("id", questionId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

}




