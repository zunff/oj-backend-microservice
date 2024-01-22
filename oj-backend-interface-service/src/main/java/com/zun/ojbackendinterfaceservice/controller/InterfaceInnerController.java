package com.zun.ojbackendinterfaceservice.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zun.ojbackendinterfaceservice.service.InterfaceInfoService;
import com.zun.ojbackendinterfaceservice.service.UserInterfaceInfoService;
import com.zun.ojbackendmodel.model.dto.interfaceinfo.InvokeCountRequest;
import com.zun.ojbackendmodel.model.entity.InterfaceInfo;
import com.zun.ojbackendmodel.model.entity.UserInterfaceInfo;
import com.zun.ojbackendserviceclient.service.InterfaceFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class InterfaceInnerController implements InterfaceFeignClient {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    /**
     * 根据Path和Method获取接口信息
     * @param url
     * @param method
     * @return
     */
    @Override
    @GetMapping("/get/url/method")
    public InterfaceInfo getByUrlAndMethod(@RequestParam("url") String url, @RequestParam("method") String method) {
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getUrl, url);
        //使用 UPPER 函数进行忽略大小写匹配
        if (StrUtil.isNotBlank(method)) {
            queryWrapper.apply("UPPER(method) = UPPER({0})", method);
        }
        return interfaceInfoService.getOne(queryWrapper);
    }

    /**
     * 接口调用统计接口
     * @param invokeCountRequest
     * @return
     */
    @Override
    @PostMapping("/user/invoke/count")
    public boolean invokeCount(@RequestBody InvokeCountRequest invokeCountRequest) {
        return userInterfaceInfoService.invokeCount(invokeCountRequest);
    }

    /**
     * MyBatisPlus方法
     * @param accessKey
     * @param interfaceId
     * @return
     */
    @Override
    @GetMapping("/user/get/accessKey/interfaceId")
    public UserInterfaceInfo getUserInterfaceInfo(@RequestParam("accessKey") String accessKey, @RequestParam("interfaceId") Long interfaceId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        queryWrapper.eq("interfaceId", interfaceId);
        return userInterfaceInfoService.getOne(queryWrapper);
    }

    /**
     * MyBatisPlus方法
     * @param userInterfaceInfo
     * @return
     */
    @Override
    @PostMapping ("/user/save")
    public boolean saveUserInterfaceInfo(@RequestBody UserInterfaceInfo userInterfaceInfo) {
        return userInterfaceInfoService.save(userInterfaceInfo);
    }

    /**
     * MyBatisPlus方法
     * @param userInterfaceInfo
     * @return
     */
    @Override
    @PostMapping ("/user/update/id")
    public boolean updateUserInterfaceInfoById(@RequestBody UserInterfaceInfo userInterfaceInfo) {
        return userInterfaceInfoService.updateById(userInterfaceInfo);
    }
}
