package com.zxxwl.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HeaderTokenCheck
 *
 * @author zhouxin
 * @apiNote 拦截器中验证Http头中的Token是否正确
 * @since 2020/10/29 10:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderTokenCheck {
    String value() default "";

    String status() default "";

    String source() default "";
}