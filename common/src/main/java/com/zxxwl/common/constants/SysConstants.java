package com.zxxwl.common.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author zhouxin
 * @apiNote 系统常量
 * @since 2020/10/29 10:46
 */
public class SysConstants {

    public static final Charset DefaultCharset = StandardCharsets.UTF_8;

    /**
     * 公司基础信息
     */
    public static final String API_COMPANY = "浙江宠派信息科技有限公司";
    public static final String API_AUTHOR_BY = "www.zxxwl.com";
    public static final String API_VERSION = "1.0.0";

    //region -------------------------------------服务通讯常量-------------------------------

    /**
     * 身份Token
     */
    public static final String REQ_HEAD_USER_TOKEN = "User-Token";
    /**
     * 防篡改Token（签名）
     */
    public static final String SAFETY_TOKEN = "Safety-Token";
    /**
     * 防篡改密明文（时间戳（毫秒））
     */
    public static final String SAFETY_CODE = "Safety-Code";
    /**
     * 防篡改Key
     */
    public static final String SAFETY_KEY = "Safety-Key";

    /**
     * 加密中间值值
     */
    public static final String SAFETY_VAL = "zxxwl";

    /**
     * 加密值 SECRET
     */
    public static final String SAFETY_SECRET = "uozAi8v$IX!5";

    /**
     * 防篡改加密秘钥(HmacSHA1方式加密)
     */
    public static final String HMAC_SHA1_KEY = "QAZxswEDCvfrTGBnhyUJMkiOLp";
    /**
     * 服务间通讯默认Token
     */
    public static final String CLIENT_DEFAULT_USER_TOKEN = "-99";

    //endregion
    /**
     *
     */
    public static final String REQ_HEAD_X_SOURCE_ID = "X-Source-Id";
}
