package com.lzs.yuntukubackend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.manager.FileManager;
import com.lzs.yuntukubackend.model.entity.Picture;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.request.picture.PictureQueryRequest;
import com.lzs.yuntukubackend.model.request.picture.PictureUploadRequest;
import com.lzs.yuntukubackend.model.vo.picture.PictureVo;
import com.lzs.yuntukubackend.model.vo.picture.PictureStructureResult;
import com.lzs.yuntukubackend.model.vo.user.UserVo;
import com.lzs.yuntukubackend.service.PictureService;
import com.lzs.yuntukubackend.mapper.PictureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 86182
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-06-13 09:15:53
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService {

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private FileManager fileManager;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Override
    public PictureVo uploadPicture(PictureUploadRequest pictureUploadRequest, User user, MultipartFile multipartFile) throws IOException {
        //1:判断用户是要新上传图片还是要更新旧有图片的信息
        ThrowUtils.throwIf(pictureUploadRequest==null, ErrorCode.PARAMS_ERROR,"前端传来的图片上传请求为空");
        Long pictureId=pictureUploadRequest.getId();
        int count=0;
        if(pictureUploadRequest.getId()!=null)
        {
            //ID不为空说明要更新图片，因此在这里要确定更新的图片确实存在于数据库
            QueryWrapper<Picture> queryWrapper=new QueryWrapper();
            queryWrapper.eq("id",pictureId);
            count = this.count(queryWrapper);
            //如果count的值等于0，说明图片并不存在
            ThrowUtils.throwIf(count==0,ErrorCode.PARAMS_ERROR,"用户想要更新图片信息，但是传入的图片ID并不存在");
        }
        //2：无论是新增还是上传图片，都需要用到FileManager的uploadFile方法，因此在这里先执行该方法
        PictureStructureResult pictureStructureResult = fileManager.uploadFile(String.format("public/%s", user.getId()), multipartFile);
        //2.1:创建Picture实体类并将其存储进数据库
        Picture picture=new Picture();
        picture.setUrl(pictureStructureResult.getPicUrl());
        picture.setName(pictureStructureResult.getPictureName());
        picture.setPicSize(pictureStructureResult.getPicSize());
        picture.setPicWidth(pictureStructureResult.getPicWidth());
        picture.setPicHeight(pictureStructureResult.getPicHeight());
        picture.setPicScale(pictureStructureResult.getPicScale());
        picture.setPicFormat(pictureStructureResult.getFormat());
        picture.setUserId(user.getId());
        //2.2:如果用户是更新图片则还需要设置图片id
       if(count==1)
       {
           picture.setId(pictureId);
           picture.setEditTime(new Date());
       }
       //将图片存储进数据库
        boolean b = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR,"图片保存进数据库失败");
        //将Picture转换为PictureVo返回给前端
        PictureVo pictureVo = PictureVo.pictureToPictureVo(picture);
        return pictureVo;
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest)
    {
        QueryWrapper<Picture> queryWrapper=new QueryWrapper<>();
        //1：检验前端传来的参数
        ThrowUtils.throwIf(pictureQueryRequest==null,ErrorCode.PARAMS_ERROR,"用户的图片查询请求为空");
        //2：获取图片查询请求当中的字段
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();

        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();

        Long userId = pictureQueryRequest.getUserId();
        String searchText = pictureQueryRequest.getSearchText();

        String orderField = pictureQueryRequest.getOrderField();
        String sortedOrder = pictureQueryRequest.getSortedOrder();
        //3：将字段传入queryWrapper当中构建查询条件
        queryWrapper.eq(id!=null,"id",id);
        queryWrapper.eq(userId!=null,"userId",userId);
        queryWrapper.eq(picSize!=null,"picSize",picSize);
        queryWrapper.eq(picWidth!=null,"picWidth",picWidth);
        queryWrapper.eq(picHeight!=null,"picHeight",picHeight);
        queryWrapper.eq(picScale!=null,"picScale",picScale);
        queryWrapper.eq(picFormat!=null,"picFormat",picFormat);
        queryWrapper.like(StrUtil.isNotBlank(name),"name",name);
        queryWrapper.like(StrUtil.isNotBlank(introduction),"introduction",introduction);
        queryWrapper.like(StrUtil.isNotBlank(category),"category",category);
        queryWrapper.orderBy(StrUtil.isNotBlank(orderField),sortedOrder.equals("asc"),orderField);
        //4：处理json查询
        if(CollectionUtil.isNotEmpty(tags))
        {
            tags.forEach((tag)->
            {
                queryWrapper.like("tags","\""+tag+"\"");
            });
        }
        //5：处理多字段查询
        if(StrUtil.isNotBlank(searchText))
        {
            queryWrapper.like("name",searchText).or().like("introduction",searchText);
        }
        return queryWrapper;
    }

    @Override
    public  List<PictureVo> pictureToPictureVo(List<Picture> list)
    {
        //1:构造出一个pictureVoList
        List<PictureVo> pictureVoList = list.stream().map(PictureVo::pictureToPictureVo).collect(Collectors.toList());
        //2：获取到所有的用户信息
        List<Long> userIdList = pictureVoList.stream().map(PictureVo::getUserId).distinct().collect(Collectors.toList());
        List<User> userList = userServiceImpl.listByIds(userIdList);
        //3:将所有的用户与他们所对应的PictureVo进行关联
        Map<Long, List<UserVo>> userIdMap = userList.stream().map(UserVo::userToUserVo).collect(Collectors.groupingBy(new Function<UserVo, Long>() {
            @Override
            public Long apply(UserVo userVo) {
                return userVo.getId();
            }
        }));
        pictureVoList.forEach((pictureVo)->
        {
            Long userId = pictureVo.getUserId();
            if (userIdMap.containsKey(userId))
            {
                pictureVo.setUserVo(userIdMap.get(userId).get(0));
            }
        });
        return pictureVoList;
    }
}




