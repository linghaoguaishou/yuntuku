package com.lzs.test.exception;

import lombok.Getter;

@Getter
public enum ErrodCode{

    LOGIN_ERROR(40001,"用户登录失败");
    private int code;
    private String message;

    ErrodCode(int code, String message)
    {
        this.code=code;
        this.message=message;
    }
}
