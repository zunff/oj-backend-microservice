package com.zun.ojbackenduserservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zun.ojbackendcommon.constant.UserConstant;
import com.zun.ojbackendcommon.model.qo.user.UserQueryRequest;
import com.zun.ojbackendcommon.model.entity.User;
import com.zun.ojbackendcommon.model.enums.UserRoleEnum;
import com.zun.ojbackendcommon.model.qo.user.UserRegisterRequest;
import com.zun.ojbackendcommon.model.vo.LoginUserVO;
import com.zun.ojbackendcommon.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author ZunF
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest req
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    String userLogin(String userAccount, String userPassword);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    LoginUserVO getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUser(String token);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
