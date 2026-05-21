package com.asset;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 资产管理系统启动类
 * 轻量级独立部署版本 - 适用于村卫生室等小型机构
 */
@SpringBootApplication
@EnableCaching
@MapperScan("com.asset.repository")
public class AssetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
        System.out.println("========================================");
        System.out.println("   资产管理系统启动成功！");
        System.out.println("   访问地址：http://localhost:8080/api");
        System.out.println("========================================");
    }
}
