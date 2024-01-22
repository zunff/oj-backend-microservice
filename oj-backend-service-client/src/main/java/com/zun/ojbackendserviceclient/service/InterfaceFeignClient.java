package com.zun.ojbackendserviceclient.service;


import com.zun.ojbackendmodel.model.dto.interfaceinfo.InvokeCountRequest;
import com.zun.ojbackendmodel.model.entity.InterfaceInfo;
import com.zun.ojbackendmodel.model.entity.UserInterfaceInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author ZunF
 */
@FeignClient(name = "oj-backend-interface-service", path = "/api/interface/inner")
public interface InterfaceFeignClient {

    /**
     * 根据Url和Method获取接口信息
     * @param url
     * @param method
     * @return
     */
    @GetMapping("/get/url/method")
    InterfaceInfo getByUrlAndMethod(@RequestParam("url") String url, @RequestParam("method") String method);

    /**
     * 接口调用统计接口
     * @param invokeCountRequest
     * @return
     */
    @PostMapping("/user/invoke/count")
    boolean invokeCount(@RequestBody InvokeCountRequest invokeCountRequest);

    /**
     * MyBatisPlus方法
     * @param accessKey
     * @param interfaceId
     * @return
     */
    @GetMapping("/user/get/accessKey/interfaceId")
    UserInterfaceInfo getUserInterfaceInfo(@RequestParam("accessKey") String accessKey, @RequestParam("interfaceId") Long interfaceId);

    /**
     * MyBatisPlus方法
     * @param userInterfaceInfo
     * @return
     */
    @PostMapping ("/user/save")
    boolean saveUserInterfaceInfo(@RequestBody UserInterfaceInfo userInterfaceInfo);

    /**
     * MyBatisPlus方法
     * @param userInterfaceInfo
     * @return
     */
    @PostMapping ("/user/update/id")
    boolean updateUserInterfaceInfoById(@RequestBody UserInterfaceInfo userInterfaceInfo);
}
