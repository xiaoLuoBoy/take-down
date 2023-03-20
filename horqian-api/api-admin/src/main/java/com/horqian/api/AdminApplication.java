package com.horqian.api;

import cn.shuibo.annotation.EnableSecurity;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author bz
 * @date 2021/06/15
 * @description
 */
@SpringBootApplication
@EnableSecurity
@EnableScheduling
@MapperScan("com.horqian.api.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
