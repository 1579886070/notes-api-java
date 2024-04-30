package com.zxxwl.login;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


//@EnableScheduling
@ComponentScan(basePackages = {"com.zxxwl.**"})
@MapperScan({
        "com.zxxwl.**.mapper",
        "com.baomidou.*.*.mapper"
})
@SpringBootApplication
public class LoginMainApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(LoginMainApp.class, args);
    }
}