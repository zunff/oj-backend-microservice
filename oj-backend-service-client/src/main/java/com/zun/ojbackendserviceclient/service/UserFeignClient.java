package com.zun.ojbackendserviceclient.service;

import com.zun.ojbackendcommon.constant.UserConstant;
import com.zun.ojbackendmodel.model.entity.User;
import com.zun.ojbackendmodel.model.enums.UserRoleEnum;
import com.zun.ojbackendmodel.model.vo.LoginUserVO;
import com.zun.ojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
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
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(@RequestBody HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            return null;
        }
        return getLoginUser(user);
    }

    /**
     * 替换掉request参数
     * @param currentUser
     * @return
     */
    @PostMapping("/get/login")
    User getLoginUser(@RequestBody User currentUser);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    default boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

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

}
