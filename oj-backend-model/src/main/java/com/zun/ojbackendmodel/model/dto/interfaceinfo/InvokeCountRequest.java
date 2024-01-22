package com.zun.ojbackendmodel.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokeCountRequest implements Serializable {

    /**
     * 用户编号
     */
    private String accessKey;

    /**
     * 接口编号
     */
    private long interfaceId;

    private static final long serialVersionUID = 1L;
}
