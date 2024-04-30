package com.zxxwl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * api 请求 限制 默认 800毫秒 内请求一次
 * 必须登录才能使用
 *
 * @author qingyu 2023.05.12
 * @apiNote 基于 AOP： 适用于controller（API），service 方法上（目前需要登录），功能 稍后访问
 * {link  com.zxxwl.user.service.aspect.ApiReqLimitAspect}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiReqLimit {
    /**
     * 毫秒 默认800ms
     */
    long ms() default 800;

    /**
     * 描述
     */
    String note() default "";
}
