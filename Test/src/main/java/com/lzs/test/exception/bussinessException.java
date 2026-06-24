package com.lzs.test.exception;

public class bussinessException extends RuntimeException
{
    private int code;

    public bussinessException(int code,String message)
    {
        super(message);
        this.code=code;
    }

    public bussinessException(ErrodCode errodCode)
    {
       this(errodCode.getCode(), errodCode.getMessage());
    }

    public bussinessException(ErrodCode errodCode,String message)
    {
        this(errodCode.getCode(),message);
    }
}
