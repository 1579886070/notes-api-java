package com.zxxwl.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.zxxwl.common.api.sys.thirdaccount.mapper"})
@MapperScan("com.zxxwl.common.api.sys.thirdaccount.mapper")
@Configuration
public class AppScan {
}
