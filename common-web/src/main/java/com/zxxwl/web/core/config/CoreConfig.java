package com.zxxwl.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;

@Configuration
public class CoreConfig {
    @Bean
    public FormContentFilter formContentFilter() {
        return new FormContentFilter();
    }
}