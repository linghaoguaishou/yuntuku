package com.lzs.yuntukubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzs.yuntukubackend.model.request.user.*;
import com.lzs.yuntukubackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzs.yuntukubackend.model.vo.user.UserLoginVo;
import com.lzs.yuntukubackend.model.vo.user.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86182
* @description 针对表【user_center(用户表)】的数据库操作Service
* @createDate 2026-04-22 20:59:46
*/
public interface UserService extends IService<User> {

    long userRegister(UserRegisterRequest userRegisterRequest);

    String getEncryptString(String initialString);

    UserLoginVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    UserLoginVo userToLoginVo(User user);

    User getUserLogin(HttpServletRequest request);

    boolean OutUserLogin(HttpServletRequest request);

    long addUser(UserAddRequest userAddRequest);

    boolean updateaUser(UserUpdateRequest userUpdateRequest);

    UserVo getUserVo(User user);

    List<UserVo> getUserVoList(List<User> list);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
