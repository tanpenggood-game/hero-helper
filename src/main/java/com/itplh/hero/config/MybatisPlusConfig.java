package com.itplh.hero.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.itplh.hero.mapper")
public class MybatisPlusConfig {
}
