package com.zun.ojbackendquestionservice.controller;

import com.zun.ojbackendmodel.model.entity.Question;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import com.zun.ojbackendquestionservice.service.QuestionService;
import com.zun.ojbackendquestionservice.service.QuestionSubmitService;
import com.zun.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public Question getById(@RequestParam("id") Long id) {
        return questionService.getById(id);
    }

    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @Override
    @GetMapping("/submit/get/id")
    public QuestionSubmit getSubmitById(@RequestParam("id") Long id) {
        return questionSubmitService.getById(id);
    }

    /**
     * MybatisPlus方法
     * @param entity
     * @return
     */
    @Override
    @PostMapping("/update/id")
    public boolean updateSubmitById(QuestionSubmit entity) {
        return questionSubmitService.updateById(entity);
    }
}
