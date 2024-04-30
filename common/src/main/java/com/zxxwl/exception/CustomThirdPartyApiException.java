package com.zxxwl.exception;

import lombok.Getter;

/**
 * 自定义 调用第三方 API 异常
 *
 * @author qingyu 2023.03.10
 */
@Getter
public class CustomThirdPartyApiException extends RuntimeException {
    /**
     * 访问api地址
     */
    private String api;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 错误码
     */
    private int code;


    /**
     * @param api     api 地址
     * @param message msg
     * @param code    code
     */
    public CustomThirdPartyApiException(String api, String message, int code) {
        super(message);
        this.api = api;
        this.message = message;
        this.code = code;
    }


    /**
     * @param cause   Throwable
     * @param api     api 地址
     * @param message msg
     * @param code    code
     */
    public CustomThirdPartyApiException(Throwable cause, String api, String message, int code) {
        super(cause);
        this.api = api;
        this.message = message;
        this.code = code;
    }
}
