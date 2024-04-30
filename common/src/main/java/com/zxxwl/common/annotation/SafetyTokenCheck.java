package com.zxxwl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SafetyTokenCheck
 *
 * @author zhouxin
 * @apiNote 拦截器中验证客户端请求头是否符合规定传参，防止抓包篡改
 * @since 2023/05/16 10:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SafetyTokenCheck {
    String value() default "";

    String status() default "";

    String source() default "";
}