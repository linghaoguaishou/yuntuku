package com.lzs.yuntukubackend.common;

import lombok.Data;

/**
 * 这个类的主要作用是提供一个统一的返回结果包装
 * 为什么需要这个类呢？
 * 我的理解是在当前主流的前后端分离开发模式下，前端调用接口，后端返回json格式数据。在这种情况下，
 * 后端程序员需要在json格式的数据能够清楚地描述这次调用的具体结果，例如成功还是失败，成功的话需要有
 * 具体的数据，失败的话也需要向前端描述失败的情况。这些数据自然就需要用一个类来整体表示。
 */
@Data
public class BaseResponse<T>
{
    /**
     * 状态码
     */
    private int code;
    /**
     * 返回的具体数据
     */
    private T data;
    /**
     * 对当前情况的具体描述
     */
    private String message;

    public BaseResponse(int code,T data,String message)
    {
        this.code=code;
        this.data=data;
        this.message=message;
    }
}
