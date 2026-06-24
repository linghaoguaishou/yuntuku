package com.lzs.yuntukubackend.model.request.picture;

import com.lzs.yuntukubackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureQueryRequest extends PageRequest implements Serializable
{
    private static final long serialVersionUID = 4286807546089318910L;

    private Long id;
    private String name;
    private String introduction;
    private String category;
    private List<String> tags;
    private Long picSize;
    private Integer picWidth;
    private Integer picHeight;
    private Double picScale;
    private String picFormat;
    private Long userId;
    private String searchText;
}
