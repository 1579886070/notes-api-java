package com.zxxwl.common.constants;

/**
 * 阿里云常量
 *
 * @author zhouxin
 * @since 2020/11/7 15:30
 */
public class ALYConstants {
    /**
     * OSS常量
     */
    public static final String OSS_STATIC_POSITION = "zxxwl-xiaota";
    public static final String OSS_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    public static final String OSS_ACCESS_KEY = "";
    public static final String OSS_ACCESS_SECRET = "";
    public static final String OSS_STATIC_MAIN_LINK = "https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/";
    public static final String OSS_PUBLIC_PICTURE = "resources/";
    // 短链存储目录
    public static final String OSS_PUBLIC_PICTURE_SHORT = "short/";
    /**
     * 阿里oss-跟路径-小它服务
     */
    public static final String OSS_PUBLIC_ROOT_TFW = "zxxwl/";

    /**
     * SMS常量
     */
    public static final String SMS_ACCESS_KEY = "";
    public static final String SMS_ACCESS_SECRET = "";
    public static final String ALIBABA_CLOUD_ACCESS_KEY_ID = "";
    public static final String ALIBABA_CLOUD_ACCESS_KEY_SECRET = "";
    public static final String SMS_SIGN_NAME_CP = "浙江宠派信息科技";
    public static final String SMS_SIGN_NAME_XT = "小它";
    public static final String SMS_SIGN_NAME_LLT = "遛遛它";
    public static final String SMS_SEND_SUCCESS = "OK";
    public static final String SMS_REGION_ID = "cn-hangzhou";
    public static final String SMS_ENDPOINT = "cn-hangzhou";
    // 身份验证验证码
    public static final String SMS_CODE_IDENTITY_VERIFICATION_CP = "SMS_205255754";


    /**
     * 号码认证
     */
    public static final String PHONE_AUTH_ACCESS_KEY = "";
    public static final String PHONE_AUTH_ACCESS_SECRET = "";

    /**
     * 阿里云高德地理编码服务
     */
    public static final String GAO_DE_GEO_URL = "http://geo.market.alicloudapi.com/v3/geocode/geo?";
    public static final String GAO_DE_GEO_APP_CODE = "";

    /**
     * 支付宝请求地址
     */
    public static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com/gateway.do";
//    public static final String ALIPAY_SERVER_URL = "https://openapi.alipaydev.com/gateway.do";
    /**
     * 支付宝APPID
     */
    public static final String ALIPAY_APP_ID = "";
//    public static final String ALIPAY_APP_ID = "";
    /**
     * 支付宝公钥
     */
    public static final String PUBLIC_KEY = "";
//    public static final String PUBLIC_KEY = "";
    /**
     * 商户私钥
     */
    public static final String PRIVATE_KEY = "";
//    public static final String PRIVATE_KEY = "";

    /**
     * 阿里云身份认证
     */
    //实名认证地址
    public static final String MARKET_ID_CARD_HOST = "https://eid.shumaidata.com";
    public static final String MARKET_ID_CARD_PATH = "/eid/check";

    //地址转经纬度
    public static final String MARKET_MAP_GEOCODE_HOST = "https://qryloct.market.alicloudapi.com";
    //地址逆解析（地址转经纬度）
    public static final String MARKET_MAP_GEOCODE_ADDRESS = "/lundear/qryaddr";
    //经纬度转地址
    public static final String MARKET_MAP_GEOCODE_LNG_LAT_ADDRESS = "/lundear/qryloct";

}
