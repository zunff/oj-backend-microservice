package com.zun.ojbackendcommon.model.qo.interfaceinfo;

import lombok.Data;

@Data
public class OnlineInvokeApiRequest {

    /**
     * 接口编号
     */
    private Long id;

    /**
     * 请求参数
     */
    private String requestParam;
}
