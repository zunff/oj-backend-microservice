package com.zun.ojbackendmodel.model.dto.interfaceinfo;

import com.zun.ojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *
 * @author ZunF
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;


    /**
     * 接口路径
     */
    private String url;

    /**
     * 接口状态，0关闭，1开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;


    /**
     * 查询关键字
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}