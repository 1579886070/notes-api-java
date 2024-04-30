package com.zxxwl.common.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 恶意刷新限制
 *
 * @author qingyu 2023.03.02
 * @apiNote 基于拦截器：目前仅支持需要登录的API ，功能：稍后访问；强制退出；加入黑名单；
 * 需结合 MaliceRefreshLimitInterceptor
 * {link com.zxxwl.user.service.interceptor.MaliceRefreshLimitInterceptor}
 * {link com.zxxwl.user.service.config.InterceptorConfig}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaliceRefreshLimit {
    /**
     * 时间区间
     * 单位秒
     *
     * @return seconds
     */
    int seconds() default 3;


    /**
     * 是否需要登录
     *
     * @return needLogin
     */
    boolean needLogin() default true;

    /**
     * 稍后 查看 阈值
     *
     * @return visitLaterCount
     */
    int visitLaterCount() default 5;

    /**
     * @return forcedLogOut
     * @deprecated 无用
     */
    @Deprecated
    boolean forcedLogOut() default false;

    /**
     * @return forcedLogOutCount
     * @deprecated 无用
     */
    @Deprecated
    int forcedLogOutCount() default 10;

    /**
     * 是否加入黑名单
     *
     * @return joinBlacklist
     */
    boolean joinBlacklist() default false;

    /**
     * 加入黑名单 阈值
     *
     * @return joinBlacklistCount
     */
    int joinBlacklistCount() default 10;
}
