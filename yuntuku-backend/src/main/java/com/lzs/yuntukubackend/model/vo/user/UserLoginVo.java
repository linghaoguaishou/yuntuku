package com.lzs.yuntukubackend.model.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserLoginVo implements Serializable
{
    private static final long serialVersionUID = -5364861567556292232L;

    /**
     * 用户id
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

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 用户信息更新时间
     */
    private Date updateTime;

}
