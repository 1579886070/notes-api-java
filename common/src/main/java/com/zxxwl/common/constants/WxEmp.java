package com.zxxwl.common.constants;

/**
 * @author yufuqing
 * @date 2023/3/6 15:07
 */

public class WxEmp {
    /**
     * 微信请求地址
     */
    public static final String SERVICE_URL = "https://api.weixin.qq.com";
    /**
     * 小程序Id
     */
    public static final String APPID = "";
    /**
     * 密钥
     */
    public static final String SECRET = "";

    /**
     * 订阅发送信息参数拼接
     */
    public static final String SEND_URL = "%s/%s?access_token=%s";

    /**
     * 获取openid请求发送信息参数拼接
     */
    public static final String OPEN_ID = "%s/%s?grant_type=%s&appid=%s&secret=%s&js_code=%s";


}
