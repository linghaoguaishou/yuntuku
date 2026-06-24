package com.lzs.yuntukubackend.aop;

import com.lzs.yuntukubackend.annotation.UserRoleAnnotation;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.enums.UserRoleEnum;
import com.lzs.yuntukubackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class AuthCheck
{
    @Resource
    UserService userService;

    @Around("@annotation(userRoleAnnotation)")
    public Object AuthCheckUserLoginAndRole(ProceedingJoinPoint proceedingJoinPoint, UserRoleAnnotation userRoleAnnotation) throws Throwable
    {
        //判断用户是否登录
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User userLogin = userService.getUserLogin(request);
        ThrowUtils.throwIf(userLogin==null, ErrorCode.NOT_LOGIN_ERROR,"用户未登录，无法使用该功能");
        //获取用户身份
        String userRole = userLogin.getUserRole();
        UserRoleEnum enumByValue = UserRoleEnum.getEnumByValue(userRole);
        //必须为管理员才能使用该方法
        ThrowUtils.throwIf(enumByValue.equals(UserRoleEnum.USER),ErrorCode.NO_AUTH_ERROR,"用户不是管理员，无法使用该功能");
        //执行原方法
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }
}
