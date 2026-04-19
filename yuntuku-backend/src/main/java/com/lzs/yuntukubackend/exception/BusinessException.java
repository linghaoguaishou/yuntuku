package com.lzs.yuntukubackend.exception;


import lombok.Getter;

/**
 * 异常类
 */
@Getter
public class BusinessException extends RuntimeException
{
    /**
     * 表示错误码
     */
    private int code;

    /**
     * 最基础的异常构造方法
     * @param message 表示异常的信息
     * @param code 表示错误码
     */
    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * 通过错误码来构造异常类
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode)
    {
        this(errorCode.getMessage(),errorCode.getCode());
    }

    /**
     * 通过错误码和自定义信息来构造异常类
     * @param errorCode 错误码
     * @param message 具体异常信息
     */
    public BusinessException(ErrorCode errorCode,String message)
    {
        this(message,errorCode.getCode());
    }

}
