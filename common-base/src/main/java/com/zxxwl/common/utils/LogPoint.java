package com.zxxwl.common.utils;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD
})
public @interface LogPoint {
    String title() default "";
    String module() default "";
    String controller() default "index";
    String action() default "index";
}
