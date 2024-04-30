package com.zxxwl.exception;

/**
 * 自定义异常
 *
 * @author zhouxin
 * @since 2022/3/22 13:18
 */

public class CustomCallException extends RuntimeException {

    private String message;

    public CustomCallException() {
    }

    public CustomCallException(String message) {
        super(message);
        this.message = message;
    }

}
