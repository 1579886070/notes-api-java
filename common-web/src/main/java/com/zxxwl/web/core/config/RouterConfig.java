package com.zxxwl.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class RouterConfig{
    @Bean
    public RequestMappingHandlerMapping mapping(){
        return new RequestMappingHandlerMapping();
    }
}
