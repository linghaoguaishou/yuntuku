package com.lzs.yuntukubackend.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzs.yuntukubackend.annotation.UserRoleAnnotation;
import com.lzs.yuntukubackend.common.BaseResponse;
import com.lzs.yuntukubackend.common.DeleteRequest;
import com.lzs.yuntukubackend.common.PageRequest;
import com.lzs.yuntukubackend.common.ResponseUtils;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.mapper.UserMapper;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.enums.UserRoleEnum;
import com.lzs.yuntukubackend.model.request.user.UserAddRequest;
import com.lzs.yuntukubackend.model.request.user.UserLoginRequest;
import com.lzs.yuntukubackend.model.request.user.UserRegisterRequest;
import com.lzs.yuntukubackend.model.request.user.UserUpdateRequest;
import com.lzs.yuntukubackend.model.vo.user.UserLoginVo;
import com.lzs.yuntukubackend.model.vo.user.UserVo;
import com.lzs.yuntukubackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController("/")
public class UserController
{
    @Resource
    UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest)
    {
        //判断前端参数
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR,"前端传来的参数为空");
        //调用业务层方法
        long userRegister = userService.userRegister(userRegisterRequest);
        return ResponseUtils.success(userRegister);
    }

    @PostMapping("/login")
    public BaseResponse<UserLoginVo> loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request)
    {
        //判断前端传来的参数
        ThrowUtils.throwIf(userLoginRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的参数为空");
        //调用业务层方法
        UserLoginVo userLoginVo = userService.userLogin(userLoginRequest, request);
        //返回结果
        return ResponseUtils.success(userLoginVo);
    }

    @GetMapping("/get/login")

    public BaseResponse<UserLoginVo> getLoginUser(HttpServletRequest request)
    {
        //调用业务层方法
        User userLogin = userService.getUserLogin(request);
        //将信息进行脱敏处理
        return ResponseUtils.success(userService.userToLoginVo(userLogin));
    }

    @GetMapping("/loginOut")
    public BaseResponse<Boolean> outUserLogin(HttpServletRequest request)
    {
        //调用业务层方法
        boolean result = userService.OutUserLogin(request);
        return ResponseUtils.success(result,"已退出登录");
    }

    @PostMapping("/addUser")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest)
    {
        //检验前端参数是否为空
        ThrowUtils.throwIf(userAddRequest==null,ErrorCode.PARAMS_ERROR,"前端传入的管理员添加用户参数为空");
        //调用业务层方法
        long addedUser = userService.addUser(userAddRequest);
        //返回前端数据（用户id还是布尔类型的数据）
        return ResponseUtils.success(addedUser);
    }

    @PostMapping("/updateUserByAdmin")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> updateUserByAdmin(@RequestBody UserUpdateRequest userUpdateRequest)
    {
        //检验前端传来的参数是否符合要求
        ThrowUtils.throwIf(userUpdateRequest==null,ErrorCode.PARAMS_ERROR,"前端传入的管理员更新用户参数为空");
        //调用业务层方法
        return null;
    }

    @GetMapping("/queryUserByIdAdmin")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<User> queryUserById(Long userId)
    {
        //检验前端传来的参数是否符合要求
        ThrowUtils.throwIf(userId==null,ErrorCode.PARAMS_ERROR,"前端传入的用户ID为空");
        //根据ID查询用户信息
        User userById = userService.getById(userId);
        ThrowUtils.throwIf(userById==null,ErrorCode.SYSTEM_ERROR,"后端用户查询失败");
        return ResponseUtils.success(userById);
    }

    @GetMapping("/queryUserByIdUser")
    public BaseResponse<UserVo> queryUserVoById(Long userId)
    {
        //检验前端传来的参数是否符合要求
        ThrowUtils.throwIf(userId==null,ErrorCode.PARAMS_ERROR,"前端传入的用户ID为空");
        //根据ID查询用户信息
        User userById = userService.getById(userId);
        ThrowUtils.throwIf(userById==null,ErrorCode.SYSTEM_ERROR,"后端用户查询失败");
        return ResponseUtils.success(userService.getUserVo(userById));
    }

    @PostMapping("/deleteUserById")
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest)
    {
        //检验前端传来的参数是否符合要求
        ThrowUtils.throwIf(deleteRequest==null,ErrorCode.PARAMS_ERROR,"前端传入的请求为空");
        //获取用户ID
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id==null,ErrorCode.SYSTEM_ERROR,"用户ID不存在");
        //根据ID删除用户
        boolean deleteResult = userService.removeById(id);
        return ResponseUtils.success(deleteResult,"成功删除用户");
    }

    @PostMapping("/updateUser")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest)
    {
        //检查前端参数是否为空
        ThrowUtils.throwIf(userUpdateRequest==null,ErrorCode.PARAMS_ERROR,"前端传入的用户更新请求为空");
        //调用业务层方法
        boolean updateResult = userService.updateaUser(userUpdateRequest);
        return ResponseUtils.success(updateResult,"成功更新用户信息");
    }

    @PostMapping("/queryUserByPage")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<List<UserVo>> queryUserByPage(@RequestBody PageRequest pageRequest)
    {
        //1:校验前端传来的参数是否为空
        ThrowUtils.throwIf(pageRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的分页请求参数为空");
        //2：拼接MybatisPlus的分页查询
        int currentPage = pageRequest.getPage();
        int pageSize = pageRequest.getPageSize();
        String orderField = pageRequest.getOrderField();
        String sortedOrder = pageRequest.getSortedOrder();
        boolean sortedASC=true;
        if (sortedOrder.equals("desc"))
        {
            sortedASC=false;
        }
        Page<User> page=new Page<>(currentPage,pageSize);
        page.addOrder(new OrderItem(orderField,sortedASC));
        page = userService.page(page);
        //3:将User转换为UserVo
        List<User> records = page.getRecords();
        List<UserVo> userVoList=new ArrayList<>();
        records.forEach((user)->
        {
            UserVo userVo=new UserVo();
            BeanUtil.copyProperties(user,userVo);
            userVoList.add(userVo);
        });
        return ResponseUtils.success(userVoList);
    }
}
