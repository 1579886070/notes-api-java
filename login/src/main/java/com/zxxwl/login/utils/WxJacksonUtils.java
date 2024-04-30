package com.zxxwl.login.utils;

import com.fasterxml.jackson.databind.JsonNode;

public class WxJacksonUtils {
    public static String getAppId(JsonNode node) {
        return node.path("appid").asText();
    }

    public static String getSecret(JsonNode node) {
        return node.path("secret").asText();
    }

    public static String getOpenId(JsonNode node) {
        return node.path("openid").asText();
    }

    /**
     * 获取没有区号的手机号
     * @param node {code {
     *     "phone_info": {
     *         "phoneNumber":"xxxxxx",
     *         "purePhoneNumber": "xxxxxx",
     *         "countryCode": 86,
     *         "watermark": {
     *             "timestamp": 1637744274,
     *             "appid": "xxxx"
     *         }
     *     }
     * }}
     * @return phone
     */
    public static String getPhoneByPhoneInfo(JsonNode node) {
        return node.path("purePhoneNumber").asText();
    }
    public static String getPhoneCountryCodeByPhoneInfo(JsonNode node) {
        return node.path("countryCode").asText();
    }
}
