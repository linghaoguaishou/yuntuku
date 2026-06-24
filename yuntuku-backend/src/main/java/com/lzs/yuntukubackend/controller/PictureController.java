package com.lzs.yuntukubackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.lzs.yuntukubackend.annotation.UserRoleAnnotation;
import com.lzs.yuntukubackend.common.BaseResponse;
import com.lzs.yuntukubackend.common.DeleteRequest;
import com.lzs.yuntukubackend.common.ResponseUtils;
import com.lzs.yuntukubackend.exception.BusinessException;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.manager.CosManager;
import com.lzs.yuntukubackend.model.entity.Picture;
import com.lzs.yuntukubackend.model.entity.User;
import com.lzs.yuntukubackend.model.enums.UserRoleEnum;
import com.lzs.yuntukubackend.model.request.picture.PictureQueryRequest;
import com.lzs.yuntukubackend.model.request.picture.PictureUpdateAdminRequest;
import com.lzs.yuntukubackend.model.request.picture.PictureUpdateUserRequest;
import com.lzs.yuntukubackend.model.request.picture.PictureUploadRequest;
import com.lzs.yuntukubackend.model.vo.picture.PictureVo;
import com.lzs.yuntukubackend.model.vo.user.UserVo;
import com.lzs.yuntukubackend.service.PictureService;
import com.lzs.yuntukubackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.utils.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController("/picture")
public class PictureController
{
    @Resource
    private CosManager cosManger;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @PostMapping("/add")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<String> addPicture(@RequestPart("file")MultipartFile multipartFile){
        //
        String originalFilename = multipartFile.getOriginalFilename();
        String fileKey=String.format("/test/%s",originalFilename);
        File file=null;
        try {
            file=File.createTempFile(fileKey,null);
            multipartFile.transferTo(file);
            cosManger.cosPicture(fileKey,file);
            return ResponseUtils.success(fileKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(file!=null)
            {
                boolean delete = file.delete();
                if(!delete)
                {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件删除失败");
                }

            }
        }
    }

    @GetMapping("/download")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public void downloadFile(String pictureKey, HttpServletResponse httpServletResponse) throws IOException
    {
        InputStream inputStream=null;
        try {
            COSObject cosObject = cosManger.downloadObject(pictureKey);
            inputStream=cosObject.getObjectContent();
            byte[] bytes= IOUtils.toByteArray(inputStream);
            //设置响应头
            httpServletResponse.setContentType("application/octet-stream;charset=UTF-8");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + pictureKey);
            //将输入流写入到响应当中
            httpServletResponse.getOutputStream().write(bytes);
            httpServletResponse.getOutputStream().flush();
        }catch (Exception e)
        {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"系统下载文件失败"+e.getMessage());
        }
        finally {
            if (inputStream!=null)
            {
                inputStream.close();
            }
        }
    }

    @PostMapping("/uploadfile")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<PictureVo> uploadFile(PictureUploadRequest pictureUploadRequest,
                                              HttpServletRequest httpServletRequest,
                                              @RequestPart("file")MultipartFile multipartFile) throws IOException {
        //1：获取当前登陆的用户
        User userLogin = userService.getUserLogin(httpServletRequest);
        //2:调用服务层方法实现文件上传
        PictureVo pictureVo = pictureService.uploadPicture(pictureUploadRequest, userLogin, multipartFile);
        //3:将结果返回给前端
        return ResponseUtils.success(pictureVo);
    }

    @PostMapping("/deletePicture")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> deletePicture(@RequestBody DeleteRequest deleteRequest)
    {
        //1:检验前端传来的参数
        ThrowUtils.throwIf(deleteRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的参数为空");
        //2:检验图片id是否存在
        Long id = deleteRequest.getId();
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture==null,ErrorCode.SYSTEM_ERROR,"用户想要删除的图片并不存在");
        //3:如果图片存在，那么就删除图片
        boolean deleteResult = pictureService.removeById(id);
        ThrowUtils.throwIf(!deleteResult,ErrorCode.SYSTEM_ERROR,"系统内部删除图片失败");
        return ResponseUtils.success(id,"用户删除图片成功");
    }

    @PostMapping("/updatePictureAdmin")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> updatePictureAdmin(@RequestBody PictureUpdateAdminRequest pictureUpdateAdminRequest)
    {
        //1：检验前端传来的参数是否为空
        ThrowUtils.throwIf(pictureUpdateAdminRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的更新请求为空");
        //2：获取请求当中的字段
        Long id = pictureUpdateAdminRequest.getId();
        String name = pictureUpdateAdminRequest.getName();
        String introduction = pictureUpdateAdminRequest.getIntroduction();
        String category = pictureUpdateAdminRequest.getCategory();
        List<String> tags = pictureUpdateAdminRequest.getTags();
        //3：查看前端传入的图片是否存在
        Picture picture= pictureService.getById(id);
        ThrowUtils.throwIf(picture==null,ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的ID在数据表当中不存在");
        //4：检验前端传来的名称、描述、分类不为空
        ThrowUtils.throwIf(StrUtil.isAllEmpty(name,introduction,category),ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的名称、描述、分类为空");
        //5：检验前端传来的标签列表是否为为空
        ThrowUtils.throwIf(CollectionUtil.isNotEmpty(tags),ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的标签列表为空");
        //6：调用MybatisPlus的Service接口方法，实行更新操作
        Picture newPicture=new Picture();
        BeanUtil.copyProperties(pictureUpdateAdminRequest,newPicture);
        boolean result = (Boolean)pictureService.updateById(newPicture);
        //7：返回结果
        return ResponseUtils.success(result,"管理员更新图片信息成功");
    }

    @PostMapping("/updatePictureUser")
    public BaseResponse<Boolean> updatePictureUser(@RequestBody PictureUpdateUserRequest pictureUpdateUserRequest)
    {
        //1：检验前端传来的参数是否为空
        ThrowUtils.throwIf(pictureUpdateUserRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的更新请求为空");
        //2：获取请求当中的字段
        Long id = pictureUpdateUserRequest.getId();
        String name = pictureUpdateUserRequest.getName();
        String introduction = pictureUpdateUserRequest.getIntroduction();
        String category = pictureUpdateUserRequest.getCategory();
        List<String> tags = pictureUpdateUserRequest.getTags();
        //3：查看前端传入的图片是否存在
        Picture picture= pictureService.getById(id);
        ThrowUtils.throwIf(picture==null,ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的ID在数据表当中不存在");
        //4：检验前端传来的名称、描述、分类不为空
        ThrowUtils.throwIf(StrUtil.isAllEmpty(name,introduction,category),ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的名称、描述、分类为空");
        //5：检验前端传来的标签列表是否为为空
        ThrowUtils.throwIf(CollectionUtil.isNotEmpty(tags),ErrorCode.PARAMS_ERROR,"前端传来的更新请求当中的标签列表为空");
        //6：调用MybatisPlus的Service接口方法，实行更新操作
        Picture newPicture=new Picture();
        BeanUtil.copyProperties(pictureUpdateUserRequest,newPicture);
        boolean result = (Boolean)pictureService.updateById(newPicture);
        //7：返回结果
        return ResponseUtils.success(result,"用户更新图片信息成功");
    }

    @PostMapping("/queryPictureAdmin")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Page<Picture>> queryPictureAdmin(@RequestBody PictureQueryRequest pictureQueryRequest)
    {
        //1:检验前端传来的查询请求是否为空
        ThrowUtils.throwIf(pictureQueryRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的查询图片请求为空");
        //2：分页查询
        int page = pictureQueryRequest.getPage();
        int pageSize = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage=new Page<>(page,pageSize);
        pictureService.page(picturePage,pictureService.getQueryWrapper(pictureQueryRequest));
        //3:返回分页结果
        return ResponseUtils.success(picturePage);
    }

    @PostMapping("/queryPictureUser")
    public BaseResponse<Page<PictureVo>> queryPictureUser(@RequestBody PictureQueryRequest pictureQueryRequest)
    {
        //1:检验前端传来的查询请求是否为空
        ThrowUtils.throwIf(pictureQueryRequest==null,ErrorCode.PARAMS_ERROR,"前端传来的查询图片请求为空");
        //2:检验当前的图片，自己是否有查询的资格（可能图片未审核、或者审核失败）
        //3：分页查询
        int page = pictureQueryRequest.getPage();
        int pageSize = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage=new Page<>(page,pageSize);
        pictureService.page(picturePage,pictureService.getQueryWrapper(pictureQueryRequest));
        List<Picture> pictureList = picturePage.getRecords();
        //4:将pictureList当中的Picture转换为PictureVo,并关联UserVo
        List<PictureVo> pictureVoList = pictureService.pictureToPictureVo(pictureList);
        //5：创建一个Page<PictureVo>,然后填充数据
        long total = picturePage.getTotal();
        Page<PictureVo> pictureVoPage=new Page<>(page,pageSize,total);
        pictureVoPage.setRecords(pictureVoList);
        return ResponseUtils.success(pictureVoPage,"用户分页查询成功");
    }

    @PostMapping("/queryPictureByIdAdmin")
    @UserRoleAnnotation(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Picture> queryPictureByIdAdmin(long id )
    {
        //1：检验前端传来的参数是否为空
        ThrowUtils.throwIf(id<=0,ErrorCode.PARAMS_ERROR,"前端传来的图片Id不符合格式");
        //2：根据ID查询数据库当中是否存在该图片
        Picture pictureById = pictureService.getById(id);
        ThrowUtils.throwIf(pictureById==null,ErrorCode.SYSTEM_ERROR,"后端根据ID查询数据库返回的图片为空，查询不到图片");
        //3：返回结果
        return ResponseUtils.success(pictureById,"管理员根据ID查询图片成功");
    }

    @PostMapping("/queryPictureByIdUser")
    public BaseResponse<PictureVo> queryPictureByIdUser(long id )
    {
        //1：检验前端传来的参数是否为空
        ThrowUtils.throwIf(id<=0,ErrorCode.PARAMS_ERROR,"前端传来的图片Id不符合格式");
        //2：根据ID查询数据库当中是否存在该图片
        Picture pictureById = pictureService.getById(id);
        ThrowUtils.throwIf(pictureById==null,ErrorCode.SYSTEM_ERROR,"后端根据ID查询数据库返回的图片为空，查询不到图片");
        //3：将Picture封装为PictureVo
        PictureVo pictureVo = PictureVo.pictureToPictureVo(pictureById);
        //4:返回结果
        return ResponseUtils.success(pictureVo,"用户根据ID查询图片成功");
    }



}
