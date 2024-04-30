package com.zxxwl.web.core.config;

import com.zxxwl.web.core.http.Request;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request 采用spring boot bean factory 管理
 * <p>
 * 重点: 注入 HttpServletRequest,解决取值问题
 * 重点二: @RequestScope
 * </p>
 *
 * @author qingyu 2023.09.23
 */
@Configuration
public class RequestBeanConfig {
    @Autowired
    private HttpServletRequest request;

    @Value("${custom.common-web.module-name:default}")
    private String prefix;

    @Bean
    @RequestScope
    public Request customRequest() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return new Request(request, prefix);
    }
}
