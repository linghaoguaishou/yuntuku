package com.lzs.yuntukubackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable
{
    private static final long serialVersionUID = -5861159409937377978L;

    private int page;

    private int pageSize;

    private String orderField;

    private String sortedOrder;
}
