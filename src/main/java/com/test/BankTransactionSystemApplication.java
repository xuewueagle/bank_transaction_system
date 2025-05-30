package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//使用MapperScan注解指定要扫描的数据库访问接口包
@MapperScan("com.test.dao")
public class BankTransactionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionSystemApplication.class, args);
    }

}
