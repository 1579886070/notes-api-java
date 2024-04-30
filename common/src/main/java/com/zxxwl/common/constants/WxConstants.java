package com.zxxwl.common.constants;

/**
 * @author zhouxin
 * @since 2020/11/5 15:39
 */
public class WxConstants {

    /**
     * 微信通过code获取access_token
     */
    public static final String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 获取微信用户信息
     */
    public static final String WX_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 微信刷新access_token
     */
    public static final String WX_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    /**
     * 微信应用唯一标识
     */
    public static final String WX_APP_ID = "";
    /**
     * 微信应用密钥
     */
    public static final String WX_SECRET = "";

    /**
     * 默认缓存2小时
     */
    public static final String CACHE_ACCESS_TOKEN = "_accessToken";

    /**
     * 默认缓存一个月
     */
    public static final String CACHE_REFRESH_TOKEN = "refreshToken";

    /**
     * 公众账号ID
     */
    public static final String WX_PUBLIC_API_ID = "";

    /**
     * 公众账号密钥
     */
    public static final String WX_PUBLIC_SECRET = "";
    /**
     * 微信支付商户号
     */
    public static final String WX_MCH_ID = "";
    /**
     * 微信API密钥
     */
    public static final String WX_API_KEY = "";
    /**
     * 微信API V3密钥
     */
    public static final String WX_API_V3_KEY = "";


    /**
     * 微信统一下单接口
     */
    public static final String WX_SERVER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**
     * 微信证书地址
     */
    public static final String WX_CART_PATH = "/code/cert/weixin-cert/apiclient_cert.pem";
   public static final String WX_CART_PATH2 = "/code/cert/weixin-cert/apiclient_cert.p12";
    /**
     * 微信退款接口
     */
    public static final String WX_SERVER_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    /**
     * 微信企业付款接口
     */
    public static final String WX_SERVER_TRANSFER_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    /**
     * 微信 公众号 获取Access token
     * https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html
     */
    public static final String WX_SERVER_CGI_BIN_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    /**
     * 微信 JS SDK 获取ticket
     * https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html
     */
    public static final String WX_SERVER_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    /**
     * 微信小程序应用唯一标识
     */
    public static final String WX_APPLET_APP_ID = "";
    /**
     * 微信小程序应用密钥
     */
    public static final String WX_APPLET_SECRET = "";
    /**
     * 获得临时登录凭证 根据code 获取小程序openid session_key
     */
    public static final String WX_SERVER_JS_CODE2_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 接口调用凭证+code 获取手机号
     */
    public static final String WX_SERVER_GET_PHONE_NUMBER_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";
    /**
     * 获取小程序码
     */
    public static final String WX_SERVER_GET_APPLET_CODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
    /**
     * 服务端 API 域名
     * 通用域名(api.weixin.qq.com)，使用该域名将访问官方指定就近的接入点；
     * <p>
     * 通用异地容灾域名(api2.weixin.qq.com)，当上述域名不可访问时可改访问此域名；
     * <p>
     * 上海域名(sh.api.weixin.qq.com)，使用该域名将访问上海的接入点；
     * <p>
     * 深圳域名(sz.api.weixin.qq.com)，使用该域名将访问深圳的接入点；
     * <p>
     * 香港域名(hk.api.weixin.qq.com)，使用该域名将访问香港的接入点。
     */
    public static String BASE_URL = "https://api.weixin.qq.com";
    public static String BASE_URL_2 = "https://api2.weixin.qq.com";
    /**
     * 微信商户平台 商户（Merchant）
     */
    public static String MCH_BASE_URL = "https://api.mch.weixin.qq.com";
    /**
     * <p><a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getAccessToken.html">查看对应微信官方文档</a></p>
     */
    public static String CGI_BIN_TOKEN = "/cgi-bin/token";
    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-message-management/subscribe-message/sendMessage.html#%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E">sendMessage</a>
     */
    public static String CGI_BIN_MSG_SUBSCRIBE_SEND = "/cgi-bin/message/subscribe/send";
    /**
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/WeChat_Invoice/Nontax_Bill/API_list.html#2.1%20%E8%8E%B7%E5%8F%96ticket">getticket</a>
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html#62">getticket 附录1-JS-SDK使用权限签名算法</a>
     */
    public static String CGI_BIN_TICKET_GET = "/cgi-bin/ticket/getticket";
    public static String CGI_BIN_OPENID_RID_GET = "/cgi-bin/openapi/rid/get";
    /**
     * 获取用户基本信息
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html">获取用户基本信息</a>
     */

    public static String CGI_BIN_USER_INFO = "/cgi-bin/user/info";
    /**
     * <p><a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html#0">查看对应微信官方文档</a></p>
     * // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * <p>
     */
    public static String SNS_AUTH2_ACCESS_TOKEN = "/sns/oauth2/access_token";
    /**
     * 参见  SNS_AUTH2_ACCESS_TOKEN  /sns/oauth2/access_token 文档地址
     */
    public static String SNS_AUTH = "/sns/auth";
    /**
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html">/sns/userinfo</a>
     */
    public static String SNS_USERINFO = "/sns/userinfo";
    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html">小程序登录</a>
     */
    public static String SNS_CODE2_SESSION = "/sns/jscode2session";

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html">getuserphonenumber</a>
     */
    public static String WXA_PHONE_NUMBER = "/wxa/business/getuserphonenumber";
    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html#%E8%B0%83%E7%94%A8%E6%96%B9%E5%BC%8F">getwxacodeunlimit</a>
     */
    public static String WXA_UN_LIMIT_CODE = "/wxa/getwxacodeunlimit";
    /**
     * 使用场景:后台服务器调用
     */
    public static String GRANT_TYPE_CLIENT_CREDENTIAL = "client_credential";
    /**
     * 使用场景:用户认证
     */
    public static String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    /**
     * 缓存key 前缀
     */
    public static String CACHE_KEY_WX_CGI_BIN_TOKEN = "wx_access_token:";
    /**
     * 默认缓存2小时
     */
    public static final String CACHE_KEY_USER_ACCESS_TOKEN = "wx_u_accessToken:";

    /**
     * 默认缓存一个月
     */
    public static final String CACHE_KEY_USER_REFRESH_TOKEN = "wx_u_refreshToken:";
    public static final String WX_TYPE_JSAPI = "jsapi";
    public static final String WX_TYPE_WX_CARD = "wx_card";
    public static final String WX_LANG_ZH_CN = "zh_CN";
    public static final String WX_LANG_ZH_TW = "zh_TW";
    public static final String WX_LANG_EN = "en";
    /*https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/data-analysis/others/getVisitPage.html
     */
    public static String DATACUBE_VISITPAGE = "/datacube/getweanalysisappidvisitpage";
    public static final String API_DATE_FORMAT = "yyyyMMdd";

    /**
     * 下单,应收款,待收款
     * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_1.shtml">JSAPI下单</a>
     */
    public static final String JSAPI_RECEIVABLE = "/v3/pay/transactions/jsapi";
    /**
     * 微信商户平台 订单查询 v2
     */
    public static final String JSAPI_V2_PAY_ORDER_QUERY = "/pay/orderquery";
    /**
     * 微信商户平台 订单 关闭 v2
     */
    public static final String JSAPI_V2_PAY_ORDER_CLOSE = "/pay/closeorder";
    /**
     * 微信商户平台 订单 退款查询 v2
     */
    public static final String JSAPI_V2_PAY_REFUND_QUERY = "/pay/refundquery";
    /**
     * 统一下单
     */
    public static final String V2_PAY_UNIFIED_ORDER = "/pay/unifiedorder";
    /**
     * 微信成功状态
     */
    public static final String SUCCESS_STATE = "SUCCESS";
    public static final String WX_PYA_STATE_ORDER_CLOSED = "ORDERCLOSED";
    public static final String WX_PAY_TIME_FORMAT = "yyyyMMddHHmmss";

}
