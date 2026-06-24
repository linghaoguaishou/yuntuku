package com.lzs.yuntukubackend.model.request.picture;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PictureUpdateAdminRequest implements Serializable
{
    private static final long serialVersionUID = 5442023511104873921L;

    private Long id;
    private String name;
    private String introduction;
    private String category;
    private List<String> tags;
}
