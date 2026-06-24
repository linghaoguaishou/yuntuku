package com.lzs.yuntukubackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable
{
    private static final long serialVersionUID = 4170880851589285146L;

    /**
     * 用户ID
     */
    private Long id;
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
