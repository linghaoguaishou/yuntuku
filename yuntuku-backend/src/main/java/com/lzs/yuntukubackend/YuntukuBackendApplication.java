package com.lzs.yuntukubackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
@MapperScan("com.lzs.yuntukubackend.mapper")
@EnableAspectJAutoProxy
public class YuntukuBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(YuntukuBackendApplication.class, args);
    }

}
