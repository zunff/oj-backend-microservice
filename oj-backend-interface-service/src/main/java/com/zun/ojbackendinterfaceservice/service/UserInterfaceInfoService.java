package com.zun.ojbackendinterfaceservice.service;

import com.zun.ojbackendmodel.model.dto.interfaceinfo.InvokeCountRequest;
import com.zun.ojbackendmodel.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ZunF
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-01-21 16:32:02
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 调用接口统计
     * @param invokeCountRequest
     * @return
     */
    boolean invokeCount(InvokeCountRequest invokeCountRequest);
}
