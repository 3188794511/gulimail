package com.lj.gulimail.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.lj.gulimail.ware.dao")
@EnableDiscoveryClient
@EnableFeignClients("com.lj.gulimail.ware.feign")
@EnableTransactionManagement
public class GulimailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimailWareApplication.class, args);
    }

}
