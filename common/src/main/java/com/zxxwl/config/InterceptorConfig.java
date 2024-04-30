package com.zxxwl.config;

import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.annotation.MaliceRefreshLimit;
import com.zxxwl.common.interceptor.HeaderTokenCheckInterceptor;
import com.zxxwl.common.interceptor.SafetyTokenCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器组件注册
 * <p>
 * {@link HeaderTokenCheckInterceptor}
 * {@link HeaderTokenCheck}
 * </p>
 * <p>
 * {@link MaliceRefreshLimit}
 * </p>
 *
 * @author zhouxin
 * @author qingyu 2023.02.28
 */
@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final HeaderTokenCheckInterceptor headerTokenCheckInterceptor;
    private final SafetyTokenCheckInterceptor safetyTokenCheckInterceptor;



    /*
     * 当需要注入较多时 使用{@code @RequiredArgsConstructor}
     *
     * @param maliceRefreshLimitInterceptor MaliceRefreshInterceptor
     * @param headerTokenCheckInterceptor   HeaderTokenCheckInterceptor
     */
   /* public InterceptorConfig(MaliceRefreshLimitInterceptor maliceRefreshLimitInterceptor,
                             HeaderTokenCheckInterceptor headerTokenCheckInterceptor,
                             IntegralTaskEventReqInterceptor integralTaskEventReqInterceptor
    ) {
        this.maliceRefreshLimitInterceptor = maliceRefreshLimitInterceptor;
        //在拦截器之前注入，否则HeaderTokenCheckInterceptor中的@Autowired会出现null的情况
        this.headerTokenCheckInterceptor = headerTokenCheckInterceptor;
    }*/

    /*@Bean
    public HeaderTokenCheckInterceptor pagePopulationInterceptor() {
        //在拦截器之前注入，否则HeaderTokenCheckInterceptor中的@Autowired会出现null的情况
        return new HeaderTokenCheckInterceptor();
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerTokenCheckInterceptor)
                .addPathPatterns("/**")  //拦截的配置
        ;
        registry.addInterceptor(safetyTokenCheckInterceptor)
                .addPathPatterns("/**")  //拦截的配置
        ;
    }
}
