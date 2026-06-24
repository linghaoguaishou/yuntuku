package com.lzs.yuntukubackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddRequest implements Serializable
{
    private static final long serialVersionUID = -6852954581674901858L;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户管理权限：user/admin
     */
    private String userRole;



}
