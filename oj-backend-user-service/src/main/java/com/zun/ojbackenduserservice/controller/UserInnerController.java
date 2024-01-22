package com.zun.ojbackenduserservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendserviceclient.service.UserFeignClient;
import com.zun.ojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @PostMapping("/get/login")
    public User getLoginUser(@RequestBody User currentUser) {
        return userService.getLoginUser(currentUser);
    }

    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("id")Long id) {
        return userService.getById(id);
    }

    /**
     * MybatisPlus方法
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/list/ids")
    public List<User> listByIds(@RequestParam("idList")Collection<Long> idList) {
        return userService.listByIds(idList);
    }

    /**
     * 根据AccessKey获取用户
     * @param accessKey
     * @return
     */
    @Override
    @GetMapping("/get/accessKey")
    public User getByAccessKey(@RequestParam("accessKey") String accessKey) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userService.getOne(queryWrapper);
    }
}
