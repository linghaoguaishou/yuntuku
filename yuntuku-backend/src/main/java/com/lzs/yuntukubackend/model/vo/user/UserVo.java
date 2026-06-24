package com.lzs.yuntukubackend.model.vo.user;

import cn.hutool.core.bean.BeanUtil;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVo implements Serializable
{
    private static final long serialVersionUID = -4318060265997932491L;
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
     * 用户创建时间
     */
    private Date createTime;

    public static UserVo userToUserVo(User user)
    {
        //1:检验前端传来的参数是否为空
        ThrowUtils.throwIf(user==null, ErrorCode.PARAMS_ERROR,"将User转化为UserVo的方法中，方法的User参数为空");
        //2：实行User到UserVo的转换
        UserVo userVo=new UserVo();
        BeanUtil.copyProperties(user,userVo);
        return userVo;
    }
}
