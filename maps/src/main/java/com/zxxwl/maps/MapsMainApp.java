package com.zxxwl.maps;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.zxxwl.common.**", "com.zxxwl.config"/*,"com.zxxwl.common.api.sys.thirdaccount.mapper"*/})
@MapperScan({
        "com.zxxwl.**.mapper",
        "com.zxxwl.common.**.mapper",
        "com.baomidou.**.mapper"
        /*,"com.zxxwl.common.api.sys.thirdaccount.mapper"*/
})
@SpringBootApplication
public class MapsMainApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MapsMainApp.class, args);
    }
}