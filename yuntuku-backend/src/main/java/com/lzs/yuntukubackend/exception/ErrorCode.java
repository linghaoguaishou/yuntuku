package com.lzs.yuntukubackend.exception;


import lombok.Getter;

/**
 * 自定义错误码
 * 为什么需要专门定义错误码？
 * 我的理解是：之前提到过封装结果类一般就是调用成功与调用失败，成功的情况下只需要数据即可，状态码和描述相对简单。
 * 但是调用失败的情况就与调用成功不同了，总的来说错误码有以下特点
 * 1：多种错误
 * 2：错误存在重复现象
 * 因此可以使用枚举来更方便地表示
 */
@Getter
public enum ErrorCode
{
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 对应通用响应类当中的状态码
     */
    private final int code;

    /**
     * 对应通用响应类当中的消息
     */
    private final String message;

    ErrorCode(int code,String message)
    {
        this.code=code;
        this.message=message;
    }

}
