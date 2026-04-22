package com.lzs.yuntukubackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lzs.yuntukubackend.mapper")
public class YuntukuBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuntukuBackendApplication.class, args);
    }

}
