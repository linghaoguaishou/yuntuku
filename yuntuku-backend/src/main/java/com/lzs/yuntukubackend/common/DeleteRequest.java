package com.lzs.yuntukubackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable
{
    private static final long serialVersionUID = 1276930908499409279L;

    private Long id;
}
