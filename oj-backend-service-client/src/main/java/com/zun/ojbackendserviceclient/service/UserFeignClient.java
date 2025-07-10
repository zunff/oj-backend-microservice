package com.zun.ojbackendserviceclient.service;

import com.zun.ojbackendcommon.constant.UserConstant;
import com.zun.ojbackendcommon.model.entity.User;
import com.zun.ojbackendcommon.model.enums.UserRoleEnum;
import com.zun.ojbackendcommon.model.vo.LoginUserVO;
import com.zun.ojbackendcommon.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 用户服务
 *
 * @author ZunF
 */
@FeignClient(name = "oj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 获取登录用户
     *
     * @param request request
     * @return vo
     */
    default LoginUserVO getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(UserConstant.USER_LOGIN_TOKEN);
        return getLoginUser(token);
    }

    /**
     * 获取当前登陆用户
     *
     * @param token token
     * @return
     */
    @PostMapping("/get/login")
    LoginUserVO getLoginUser(String token);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    default boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        LoginUserVO loginUser = getLoginUser(request);
        return isAdmin(loginUser);
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(LoginUserVO user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * MybatisPlus方法
     * @param id
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("id") Long id);

    /**
     * MybatisPlus方法
     * @param idList
     * @return
     */
    @GetMapping("/list/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 根据AccessKey获取用户
     * @param accessKey
     * @return
     */
    @GetMapping("/get/accessKey")
    User getByAccessKey(@RequestParam("accessKey") String accessKey);
}
