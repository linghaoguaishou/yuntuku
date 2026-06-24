package com.lzs.yuntukubackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzs.yuntukubackend.constant.UserState;
import com.lzs.yuntukubackend.exception.BusinessException;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.model.enums.UserRoleEnum;
import com.lzs.yuntukubackend.model.request.user.*;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.vo.user.UserLoginVo;
import com.lzs.yuntukubackend.model.vo.user.UserVo;
import com.lzs.yuntukubackend.service.UserService;
import com.lzs.yuntukubackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86182
* @description 针对表【user_center(用户表)】的数据库操作Service实现
* @createDate 2026-04-22 20:59:46
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        //1:检验前端传过来的参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userCheckPassword = userRegisterRequest.getUserCheckPassword();
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, userCheckPassword), ErrorCode.PARAMS_ERROR, "账号、密码、检验码不能为空");
        ThrowUtils.throwIf(userAccount.length() > 9, ErrorCode.PARAMS_ERROR, "账号格式错误");
        ThrowUtils.throwIf(userPassword.length() > 20 || userPassword.length() < 9, ErrorCode.PARAMS_ERROR, "密码格式错误");
        ThrowUtils.throwIf(!userPassword.equals(userCheckPassword), ErrorCode.PARAMS_ERROR, "两次输入密码不同");
        //2:检验userAccount是否与数据库当中的重合
        UserMapper userMapper = this.getBaseMapper();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Integer i = userMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(i > 0, ErrorCode.PARAMS_ERROR, "账号已存在，请重新设置账号");
        //3:为密码进行加密处理
        String encryptPassward = getEncryptString(userPassword);
        //4:将用户添加到数据库当中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassward);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库操作失败，用户无法保存到数据库当中");
        }
        return user.getId();
    }

    @Override
    public String getEncryptString(String initialString) {
        final String salt = "lzs";
        return DigestUtils.md5DigestAsHex((salt + initialString).getBytes());
    }

    @Override
    public UserLoginVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        //1:检验前端传来的参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "账户或者密码为空");
        ThrowUtils.throwIf(userAccount.length() > 9, ErrorCode.PARAMS_ERROR, "账号格式错误");
        ThrowUtils.throwIf(userPassword.length() > 20 || userPassword.length() < 9, ErrorCode.PARAMS_ERROR, "密码格式错误");
        //2：根据账号和密码，来查询用户是否已注册
        String encryptString = getEncryptString(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount).eq("userPassword", encryptString);
        User user = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户");
        //3：在session当中存储用户登录态
        request.getSession().setAttribute(UserState.USERLOGIN, user);
        //4：将返回的user转换为数据封装类
        UserLoginVo userLoginVo = userToLoginVo(user);
        return userLoginVo;
    }

    @Override
    public UserLoginVo userToLoginVo(User user) {
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtil.copyProperties(user, userLoginVo);
        return userLoginVo;
    }

    @Override
    public User getUserLogin(HttpServletRequest request) {
        //1:根据名称直接在session当中获取
        Object userObject = request.getSession().getAttribute(UserState.USERLOGIN);
        User user = (User) userObject;
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        //2：为了防止期间用户修改信息，这里根据Id重新查找用户
        Long id = user.getId();
        User currentUser = userMapper.selectById(id);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.SYSTEM_ERROR, "在获取登录态的途中，用户在数据库当中被删除");
        return currentUser;
    }

    @Override
    public boolean OutUserLogin(HttpServletRequest request) {
        //1：移除session当中的登录态
        request.getSession().removeAttribute(UserState.USERLOGIN);
        return true;
    }

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        //检查前端传来的用户请求封装类内部的字段是否符合要求
        ThrowUtils.throwIf(userAddRequest.getUserAccount().length() > 10 || userAddRequest.getUserAccount().length() < 9, ErrorCode.PARAMS_ERROR, "用户账户不符合要求");
        ThrowUtils.throwIf(userAddRequest.getUserName().length() > 10, ErrorCode.PARAMS_ERROR, "用户名字不符合要求");
        ThrowUtils.throwIf(userAddRequest.getUserProfile().length() > 20, ErrorCode.PARAMS_ERROR, "用户简介过长");
        //将用户请求类转换为用户类存储进数据库
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        //为用户设置默认密码并加密
        String defaultPassWard = "123456";
        user.setUserPassword(getEncryptString(defaultPassWard));
        boolean saveResult = save(user);
        //在这里方法是可能失败的，因为增加用户的话，可能账号等已经存在，所以添加失败需要添加异常处理类
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "用户添加失败");
        return user.getId();
    }

    @Override
    public boolean updateaUser(UserUpdateRequest userUpdateRequest) {
        //检验前端传来的参数
        ThrowUtils.throwIf(userUpdateRequest.getUserName().length() > 10, ErrorCode.PARAMS_ERROR, "用户名字不符合要求");
        ThrowUtils.throwIf(userUpdateRequest.getUserProfile().length() > 20, ErrorCode.PARAMS_ERROR, "用户简介过长");
        //执行更新语句
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean updateResult = updateById(user);
        return updateResult;
    }

    @Override
    public UserVo getUserVo(User user) {
        //检查参数是否为空
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "前端传来的参数为空");
        //将前端传来的参数转换为UserVo
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public List<UserVo> getUserVoList(List<User> list) {
        //检查参数是否为空
        ThrowUtils.throwIf(list == null, ErrorCode.PARAMS_ERROR, "前端传来的参数为空");
        //将前端传来的参数转换为UserVo
        return list.stream().map(this::getUserVo).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        //检查参数是否为空
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "前端传来的参数为空");
        //获取查询请求当中的字段
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String orderField = userQueryRequest.getOrderField();
        String sortedOrder = userQueryRequest.getSortedOrder();
        //拼接QueryWrapper
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotEmpty(userAccount), "userAccount", userAccount);
        queryWrapper.eq(StrUtil.isNotEmpty(userName), "userName", userName);
        queryWrapper.eq(StrUtil.isNotEmpty(userProfile), "userProfile", userProfile);
        queryWrapper.eq(StrUtil.isNotEmpty(userRole), "userRole", userRole);
        //sortedOrder代表排序方式，orderField代表按照什么字段来排序
        //sortedOrder和orderField都为空，那就不构造查询
        if (!StrUtil.isAllEmpty(orderField, sortedOrder)) {
            //此时说明，sortedOrder和orderField存在3种可能，3：orderField存在，sortedOrder为空
            //1：两者都存在
            if (StrUtil.isNotEmpty(sortedOrder) && StrUtil.isNotEmpty(orderField)) {
                //但这里的sortedOrder似乎只能取特定值
                queryWrapper.orderBy(true, sortedOrder.equals("ASC"), sortedOrder);
            }
            //2：sortedOrder存在，orderField为空
            if (StrUtil.isNotEmpty(sortedOrder) && StrUtil.isEmpty(orderField)) {
                queryWrapper.orderBy(true, sortedOrder.equals("ASC"), "id");
            }
            //3：orderField存在，sortedOrder为空
            if (StrUtil.isNotEmpty(orderField) && StrUtil.isEmpty(sortedOrder)) {
                queryWrapper.orderByAsc(orderField);
            }
        }
        return queryWrapper;
    }
}




