package com.zun.ojbackenduserservice.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zun.ojbackendcommon.common.ErrorCode;
import com.zun.ojbackendcommon.constant.CommonConstant;
import com.zun.ojbackendcommon.constant.RedisConstant;
import com.zun.ojbackendcommon.constant.UserConstant;
import com.zun.ojbackendcommon.exception.BusinessException;
import com.zun.ojbackendcommon.manager.RedisManager;
import com.zun.ojbackendcommon.model.qo.user.UserRegisterRequest;
import com.zun.ojbackendcommon.utils.SqlUtils;
import com.zun.ojbackendcommon.model.qo.user.UserQueryRequest;
import com.zun.ojbackendcommon.model.entity.User;
import com.zun.ojbackendcommon.model.enums.UserRoleEnum;
import com.zun.ojbackendcommon.model.vo.LoginUserVO;
import com.zun.ojbackendcommon.model.vo.UserVO;
import com.zun.ojbackenduserservice.mapper.UserMapper;
import com.zun.ojbackenduserservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author ZunF
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "zun";

    @Resource
    private RedisManager redisManager;

    @Value("${user.login.ttl}")
    private Integer loginUserTtl;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验

        String userName = userRegisterRequest.getUserName();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userName, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请完整填写信息");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        String email = userRegisterRequest.getEmail();
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写邮箱，并发送验证码");
        }
        String captcha = userRegisterRequest.getCaptcha();
        if (StringUtils.isBlank(captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写验证码");
        }
        //判断验证码是否正确
        String cacheCaptcha = redisManager.get(RedisConstant.CAPTCHA_CODE + email);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先发送验证码");
        }
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            user.setEmail(email);
            user.setAccessKey(RandomUtil.randomString(16));
            user.setSecretKey(RandomUtil.randomString(32));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public String userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        String token = UUID.randomUUID().toString();
        redisManager.set(RedisConstant.USER_LOGIN_KEY + token, JSONUtil.toJsonStr(getLoginUserVO(user)),
                loginUserTtl, TimeUnit.HOURS);
        return token;
    }
    
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(UserConstant.USER_LOGIN_TOKEN);
        return getLoginUser(token);
    }

    @Override
    public LoginUserVO getLoginUser(String token) {
        // 先判断是否已登录
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String userJson = redisManager.get(RedisConstant.USER_LOGIN_KEY + token);
        if (StrUtil.isBlank(userJson)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return JSONUtil.toBean(userJson, LoginUserVO.class);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        String token = request.getHeader(UserConstant.USER_LOGIN_TOKEN);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 移除登录态
        redisManager.delete(UserConstant.USER_LOGIN_TOKEN + token);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
