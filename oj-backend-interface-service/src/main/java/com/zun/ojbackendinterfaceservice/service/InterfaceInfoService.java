package com.zun.ojbackendinterfaceservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zun.ojbackendcommon.model.qo.interfaceinfo.InterfaceInfoQueryRequest;
import com.zun.ojbackendcommon.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zun.ojbackendcommon.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口服务
 *
 * @author zunf
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest postQueryRequest);


    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo post, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> postPage, HttpServletRequest request);
}
