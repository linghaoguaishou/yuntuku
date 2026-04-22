package com.lzs.yuntukubackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求封装类
 */
@Data
public class UserRegisterRequest implements Serializable
{

    private static final long serialVersionUID = -2748317014746060218L;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 验证密码
     */
    private String userCheckPassword;
}
