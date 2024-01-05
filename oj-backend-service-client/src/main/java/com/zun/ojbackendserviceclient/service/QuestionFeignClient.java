package com.zun.ojbackendserviceclient.service;

import com.zun.ojbackendmodel.model.entity.Question;
import com.zun.ojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
* @author ZunF
*/
@FeignClient(name = "oj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @GetMapping("/get/id")
    Question getById(@RequestParam("id") Long id);

    /**
     * MybatisPlus方法
     * @return
     */
    @PostMapping("/add/accNum")
    boolean addQuestionAccNum(@RequestParam("id") Long id);
    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @GetMapping("/submit/get/id")
    QuestionSubmit getSubmitById(@RequestParam("id") Long id);

    /**
     * MybatisPlus方法
     * @param entity
     * @return
     */
    @PostMapping("/update/id")
    boolean updateSubmitById(@RequestBody QuestionSubmit entity);
}
