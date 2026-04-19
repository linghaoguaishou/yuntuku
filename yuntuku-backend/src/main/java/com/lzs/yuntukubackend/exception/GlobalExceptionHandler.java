package com.lzs.yuntukubackend.exception;


import com.lzs.yuntukubackend.common.BaseResponse;
import com.lzs.yuntukubackend.common.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    /**
     * 专门处理BusinessException的处理器
     * @param exception BusinessException
     * @return BaseResponse
     */
    @ExceptionHandler(BuilderException.class)
    public BaseResponse BusinessExceptionHandler(BusinessException exception)
    {
        log.error("BusinessException",exception);
        return ResponseUtils.error(exception.getCode(),exception.getMessage());
    }
}
