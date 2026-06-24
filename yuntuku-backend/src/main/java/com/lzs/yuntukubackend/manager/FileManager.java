package com.lzs.yuntukubackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.lzs.yuntukubackend.config.CosConfig;
import com.lzs.yuntukubackend.exception.ErrorCode;
import com.lzs.yuntukubackend.exception.ThrowUtils;
import com.lzs.yuntukubackend.model.vo.picture.PictureStructureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FileManager
{
    @Resource
    private CosManager cosManager;

    @Resource
    private CosConfig cosConfig;

    public PictureStructureResult uploadFile(String picKey, MultipartFile multipartFile) throws IOException
    {
        //检验文件是否符合要求
        checkFileSize(multipartFile);
        //业务逻辑，设置图片在存储桶当中的唯一标识和获取图片信息封装类
        String uuid = RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", picKey, uploadFilename);
        File file = null;
        try
        {
            //创建临时文件
            file = File.createTempFile(picKey, null);
            multipartFile.transferTo(file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.cosPicture(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //将图片信息封装至结果类
            PictureStructureResult pictureStructureResult = new PictureStructureResult();
            pictureStructureResult.setPicUrl(cosConfig.getHost() + "/" + uploadPath);
            pictureStructureResult.setPictureName(multipartFile.getOriginalFilename());
            pictureStructureResult.setPicSize(multipartFile.getSize());
            pictureStructureResult.setPicWidth(imageInfo.getWidth());
            pictureStructureResult.setPicHeight(imageInfo.getHeight());
            double picScale = imageInfo.getWidth() * 1.0 / imageInfo.getHeight();
            pictureStructureResult.setPicScale(picScale);
            pictureStructureResult.setFormat(imageInfo.getFormat());
            return pictureStructureResult;
        } catch (Exception e)
        {
            log.error("图片上传失败", e);
            throw  new IOException("图片上传失败",e);
        } finally
        {
            if (file != null)
            {
                boolean delete = file.delete();
                if (!delete)
                {
                    log.error("临时文件删除失败",file.getAbsoluteFile());
                }
            }
        }
    }

    private static long checkFileSize(MultipartFile multipartFile) {
        long multipartFileSize = multipartFile.getSize();
        ThrowUtils.throwIf(multipartFileSize>2*1024*1024, ErrorCode.PARAMS_ERROR,"所上传的文件大小超过要求，无法上传");
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        List<String> suffisList= Arrays.asList("jpeg","jpg","png","webp");
        ThrowUtils.throwIf(!suffisList.contains(suffix),ErrorCode.SYSTEM_ERROR,"所上传的文件格式不符合要求，无法上传");
        return multipartFileSize;
    }
}
