package com.lzs.yuntukubackend.model.request.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUpdateUserRequest implements Serializable
{
    private static final long serialVersionUID = 4167464273733080603L;
    private Long id;
    private String name;
    private String introduction;
    private String category;
    private List<String> tags;
}
