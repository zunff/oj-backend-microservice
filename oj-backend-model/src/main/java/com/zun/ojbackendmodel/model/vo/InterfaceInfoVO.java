package com.zun.ojbackendmodel.model.vo;


import com.zun.ojbackendmodel.model.entity.InterfaceInfo;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class InterfaceInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

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

    /**
     * 创建人
     */
    private UserVO user;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 对象转包装类
     *
     * @param interfaceInfo
     * @return
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
    private static final long serialVersionUID = 1L;
}
