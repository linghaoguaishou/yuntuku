package com.lzs.yuntukubackend.common;

import com.lzs.yuntukubackend.exception.ErrorCode;

/**
 * 专门用于构造响应类的工具类
 * @param <T>
 */
public class ResponseUtils<T>
{
    /**
     * 这是返回"成功"结果的构造响应工具类方法
     * @param data 表示接口所返回的数据
     * @return 响应结果类
     */
    public static <T> BaseResponse<T> success(T data)
    {
        return new BaseResponse<>(200,data,"调用接口成功，返回数据");
    }

    /**
     * 这是返回"错误"结果的构造响应工具类方法
     * @param errorCode 错误码
     * @param message 更加详细地描述错误信息
     * @return 响应结果类
     */
    public static BaseResponse error(ErrorCode errorCode,String message)
    {
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }

    /**
     * 这是返回"错误"结果的构造响应工具类方法
     * @param errorCode 错误码
     * @return 响应结果类
     */
    public static BaseResponse error(ErrorCode errorCode)
    {
       return ResponseUtils.error(errorCode,errorCode.getMessage());
    }

    /**
     * 这是返回"错误"结果的构造响应工具类方法
     * @param code 自定义的错误码
     * @param message 详细的错误信息
     * @return 响应结果类
     */
    public static BaseResponse error(int code,String message)
    {
        return new BaseResponse(code,null,message);
    }

}
