package com.lzs.test.common;

import lombok.Getter;

@Getter
public class baseResponse<T> {
    private int code;
    private T data;
    private String message;

    public baseResponse(int code,T data,String message)
    {
        this.code=code;
        this.data=data;
        this.message=message;
    }

}
