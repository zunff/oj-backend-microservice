package com.zun.ojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zun.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendmodel.model.vo.QuestionSubmitVO;


import javax.servlet.http.HttpServletRequest;

/**
* @author ZunF
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-10-17 15:23:48
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return 提交记录id
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser, HttpServletRequest request);

    /**
     * 题目提交（内部服务）
     *
     * @param questionId
     * @param userId
     * @return
     */
    int doQuestionSubmitInner(long questionId, long userId);
}
