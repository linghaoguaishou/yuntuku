package com.lzs.yuntukubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzs.yuntukubackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.request.picture.PictureQueryRequest;
import com.lzs.yuntukubackend.model.request.picture.PictureUploadRequest;
import com.lzs.yuntukubackend.model.vo.picture.PictureVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* @author 86182
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-06-13 09:15:53
*/
public interface PictureService extends IService<Picture>
{
    PictureVo uploadPicture(PictureUploadRequest pictureUploadRequest, User user, MultipartFile multipartFile) throws IOException;

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

     List<PictureVo> pictureToPictureVo(List<Picture> list);
}
