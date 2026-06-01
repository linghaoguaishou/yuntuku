package com.lzs.yuntukubackend.exception;

public class ThrowUtils
{

    /**
     *
     * @param condition 判断条件
     * @param errorCode 错误码对象
     */
    public static void throwIf(boolean condition,ErrorCode errorCode)
    {
        if(condition)
        {
            throw new BusinessException(errorCode);
        }
    }


    /**
     *
     * @param condition 判断条件
     * @param errorCode 错误码
     * @param message 自定义错误的信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode,String message)
    {
        if(condition)
        {
            throw new BusinessException(errorCode.getCode(),message);
        }
    }

}
