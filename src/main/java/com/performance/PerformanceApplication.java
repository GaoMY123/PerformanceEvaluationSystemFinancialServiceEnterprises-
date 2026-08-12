package com.performance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 金融服务类企业绩效考核系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.performance.mapper")
public class PerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApplication.class, args);
    }
}
