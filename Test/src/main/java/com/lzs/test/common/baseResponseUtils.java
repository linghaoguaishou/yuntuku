package com.lzs.test.common;

public class baseResponseUtils<T>
{
    public baseResponse success(T data)
    {
        return new baseResponse(0,data,"成功获取数据");
    }


}
