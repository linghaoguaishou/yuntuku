package com.lzs.yuntukubackend.model.vo.picture;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.model.entity.Picture;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.vo.user.UserVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureVo implements Serializable
{
    private static final long serialVersionUID = -6835065685459448048L;

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private UserVo userVo;

    //创建一个静态方法用于将Picture转换为PictureVo
    public static PictureVo pictureToPictureVo(Picture picture)
    {
        //参数检验
        if(picture==null)
        {
            return null;
        }
        //创建一个PictureVo
        PictureVo pictureVo=new PictureVo();
        BeanUtil.copyProperties(picture,pictureVo);
        //注意此时：实体类Picture的标签为String，但是在返回给前端的时候，前端希望其是一个列表
        pictureVo.setTags(JSONUtil.toList(picture.getTags(),String.class));
        return pictureVo;
    }
}
