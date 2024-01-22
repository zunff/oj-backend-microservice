package com.zun.ojbackendmodel.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 *
 * @author ZunF
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口路径
     */
    private String url;

    /**
     * 请求参数（json数组）
     */
    private String requestParam;

    /**
     * 响应体示例（json对象）
     */
    private String responseBody;

    /**
     * 接口状态，0关闭，1开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    private static final long serialVersionUID = 1L;
}