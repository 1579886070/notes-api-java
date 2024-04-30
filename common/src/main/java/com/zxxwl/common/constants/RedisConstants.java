package com.zxxwl.common.constants;

/**
 * redis常量
 *
 * @author zhouxin
 * @since 2020/11/3 10:29
 */
public class RedisConstants {
    /**
     * 用户信息，按ID为field
     */
    public static final String USER_INFO = "userInfo";

    /**
     * 用户信息，按TOKE
     */
    public static final String USER_TOKEN_INFO = "userTokenInfo";

    /**
     * 用户信息，按JWT
     */
    public static final String JWT_TOKEN_INFO = "jwtTokenInfo:";

    /**
     * 店铺geo hash key值
     */
    public static final String GEO_SHOP_CODE = "geoShopCode";

    /**
     * 店铺geo hash 默认查找距离的范围（单位KM）
     */
    public static final long GEO_SHOP_DEFAULT_DISTANCE = 5000;

    /**
     * 微信ACCESS_TOKEN key值
     */
    public static final String WX_ACCESS_TOKEN = "wxAccessToken";


    /**
     * 用户临时Token
     */
    public static final String TEMP_USER_TOKEN = "tempUserToken";

    /**
     * 百度ACCESS_TOKEN key值
     * <a href="https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjhhu"></a>
     */
    public static final String BAI_DU_ACCESS_TOKEN = "baiDuAccessToken";

    /**
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/API_Call_Limits.html"></a>
     */
    public static final String TEMP_ACCESS_TOKEN = "tempAccessToken";
}
