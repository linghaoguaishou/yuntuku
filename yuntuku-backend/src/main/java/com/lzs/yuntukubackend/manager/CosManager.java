package com.lzs.yuntukubackend.manager;

import com.lzs.yuntukubackend.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Component
public class CosManager
{
    @Resource
    private CosConfig cosConfig;

    @Resource
    private COSClient cosClient;

    public  PutObjectResult cosPicture(String key, File file)
    {
        PutObjectRequest putObjectRequest=new PutObjectRequest(cosConfig.getBucket(),key,file);
        PicOperations picOperations=new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return putObjectResult;
    }

    public COSObject downloadObject(String key) throws IOException {
        GetObjectRequest getObjectRequest=new GetObjectRequest(cosConfig.getBucket(),key);
        COSObject object = cosClient.getObject(getObjectRequest);
        return object;
    }

}
