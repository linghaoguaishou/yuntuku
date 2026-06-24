package com.lzs.yuntukubackend.model.vo.picture;

import lombok.Data;

@Data
public class PictureStructureResult
{
    private String picUrl;

    private String pictureName;

    private Long picSize;

    private int picWidth;

    private int picHeight;

    private Double picScale;

    private String format;

}
