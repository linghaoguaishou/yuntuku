package com.lzs.yuntukubackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable
{
    private static final long serialVersionUID = -3907772799403110792L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
