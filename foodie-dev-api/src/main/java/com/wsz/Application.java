package com.wsz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author by 完善者
 * @date 2020/7/26 21:24
 * @DESC
 */
@SpringBootApplication
//扫描mybatis 通用 mapper所在的包
@MapperScan(basePackages = "com.wsz.mapper")
//扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.wsz","org.n3r.idworker"})
//开启定时任务
//@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
