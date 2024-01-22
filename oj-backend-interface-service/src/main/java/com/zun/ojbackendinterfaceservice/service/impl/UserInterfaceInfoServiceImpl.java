package com.zun.ojbackendinterfaceservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendmodel.model.dto.interfaceinfo.InvokeCountRequest;
import com.zun.ojbackendmodel.model.entity.UserInterfaceInfo;
import com.zun.ojbackendinterfaceservice.service.UserInterfaceInfoService;
import com.zun.ojbackendinterfaceservice.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author ZunF
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2024-01-21 16:32:02
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo> implements UserInterfaceInfoService {

    @Override
    public boolean invokeCount(InvokeCountRequest invokeCountRequest) {
        String accessKey = invokeCountRequest.getAccessKey();
        long interfaceId = invokeCountRequest.getInterfaceId();

        if (StrUtil.isBlank(accessKey) || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return update()
                .eq("accessKey", accessKey)
                .eq("interfaceId", interfaceId)
                .setSql("totalNum=totalNum+1,leftNum=leftNum-1")
                .update();
    }
}




