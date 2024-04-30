package com.zxxwl.common.web.http;

/**
 * http响应状态
 *
 * @author zhouxin
 */
public class HttpConstants {
    /**
     * （成功）  服务器已成功处理了请求
     */
    public static final int OK = 200;

    /**
     * （已创建）  请求成功并且服务器创建了新的资源
     */
    public static final int CREATED = 201;

    /**
     * （已接受）  服务器已接受请求，但尚未处理
     */
    public static final int ACCEPTED = 202;

    /**
     * （无内容）  服务器成功处理了请求，但没有返回任何内容
     */
    public static final int NO_CONTENT = 204;

    /**
     * （错误请求） 服务器不理解请求的语法。
     */
    public static final int BAD_REQUEST = 400;

    /**
     * （未授权） 请求要求身份验证，该状态码只用在未登录的情况下
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * （禁止） 服务器拒绝请求
     */
    public static final int FORBIDDEN = 403;

    /**
     * （未找到） 服务器找不到请求的网页
     */
    public static final int NOT_FOUND = 404;

    /**
     * （未满足前提条件）该状态码只用于未实名认证的情况下
     */
    public static final int PRECONDITION_FAILED = 412;

    /**
     * （服务器内部错误）  服务器遇到错误，无法完成请求
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

}
