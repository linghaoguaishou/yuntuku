package com.lzs.yuntukubackend.model.request.user;

import com.lzs.yuntukubackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest extends PageRequest implements Serializable
{
    private static final long serialVersionUID = 2703435346573366831L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户管理权限：user/admin
     */
    private String userRole;

}
