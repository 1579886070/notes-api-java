package com.zxxwl.common.api.wx.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 微信 OpenApi 调用
 * FIXME 当返回值为对象或数组时 建议使用 Jackson 的{@code JsonNode  : ObjectNode、ArrayNode 等} 或 ALi Fastjson {@code JSONObject JSONArray } 接收
 * 拓展：后期考虑引入 fastjson2
 *
 * @author qingyu 2023.03
 * @author yufuqing
 */
public interface WxOpenApiService {
    /**
     * 获取微信 小程序、公众号 access_token
     * 7200s 有效期为两小时
     *
     * <p><a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getAccessToken.html">查看对应微信官方文档</a></p>
     * access_token 的存储与更新
     * access_token 的存储至少要保留 512 个字符空间；
     * access_token 的有效期目前为 2 个小时，需定时刷新，重复获取将导致上次获取的 access_token 失效；
     * 建议开发者使用中控服务器统一获取和刷新 access_token，其他业务逻辑服务器所使用的 access_token 均来自于该中控服务器，不应该各自去刷新，否则容易造成冲突，导致 access_token 覆盖而影响业务；
     * access_token 的有效期通过返回的 expires_in 来传达，目前是7200秒之内的值，中控服务器需要根据这个有效时间提前去刷新。在刷新过程中，中控服务器可对外继续输出的老 access_token，<strong> 此时公众平台后台会保证在5分钟内，新老 access_token 都可用，这保证了第三方业务的平滑过渡；<strong/>
     * access_token 的有效时间可能会在未来有调整，所以中控服务器不仅需要内部定时主动刷新，还需要提供被动刷新 access_token 的接口，这样便于业务服务器在API调用获知 access_token 已超时的情况下，可以触发 access_token 的刷新流程。
     * 详情可参考微信公众平台文档 《获取access_token》
     * <p>
     * 避免网络波动等因素影响 定时任务应小于7200s 2小时  -> 7000
     */
    String getAccessToken(String appId, String secret);

    /**
     * 微信网页授权
     * 变更 https://developers.weixin.qq.com/community/develop/doc/000248cdf346d88930fe62eae5b001?highLine=login
     * <p><a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html#0">查看对应微信官方文档</a></p>
     * // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * <p>
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "is_snapshotuser": 1,
     * "unionid": "UNIONID"
     * }
     * </p>
     *
     * @param appId  appId
     * @param secret secret
     * @param code   code
     * @return R
     */
    JsonNode getOAuth2(String appId, String secret, String code);

    /**
     * 小程序登录
     * 注意 官方API 返回的为 json 字符 (content-type=text/plain) 需要转换
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html">小程序登录</a>
     *
     * @param appId  appId string	是	小程序 appId
     * @param secret secret string	是	小程序 appSecret
     * @param jsCode jsCode 登录时获取的 code，可通过wx.login获取
     *               grant_type string	是	授权类型，此处只需填写 authorization_code
     * @return R
     * @apiNote result <p>
     * {@code { "openid":"xxxxxx", "session_key":"xxxxx", "unionid":"xxxxx","errcode":0, "errmsg":"xxxxx" }
     * }
     * </p>
     */
    JsonNode code2Session(String appId, String secret, String jsCode);




    /**
     * 注意 微信 接口调整
     * <p>
     * <a href="https://developers.weixin.qq.com/community/develop/doc/000cacfa20ce88df04cb468bc52801">小程序登录、用户信息相关接口调整说明</a>
     * <a href="https://developers.weixin.qq.com/community/develop/doc/00022c683e8a80b29bed2142b56c01">小程序用户头像昵称获取规则调整公告 2022-05-09</a>
     * </p>
     *
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openid      用户的唯一标识
     * @param lang        返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return R
     */
    JsonNode getUserInfoByOAuth2(String accessToken, String openid, String lang);

    /**
     * 检验授权凭证（access_token）是否有效 ,注意用于校验 微信网页授权("/sns/oauth2")
     *
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openid      用户的唯一标识
     * @return R
     */
    String ckOAuth2(String accessToken, String openid);

    /**
     * 强制更新并缓存
     *
     * @return AccessToken
     */
    String upAndCacheAccessToken(String appId, String secret);

    /**
     * 发送订阅消息
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-message-management/subscribe-message/sendMessage.html#%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E">sendMessage</a>
     *
     * @param bodyValue bodyValue
     * @return R
     */
    JsonNode sendMessage(ObjectNode bodyValue, String accessToken);

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html">getuserphonenumber</a>
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html"> 获取手机号 </a>
     * @apiNote {@code {
     *     "errcode":0,
     *     "errmsg":"ok",
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
     * @param code        code	String	动态令牌。可通过动态令牌换取用户手机号。使用方法详情 phonenumber.getPhoneNumber 接口
     * @param accessToken 接口调用凭证，该参数为 URL 参数，非 Body 参数。使用access_token或者authorizer_access_token
     * @return phone
     */
    JsonNode getPhoneByCode(String code, String accessToken);

    /**
     * getAppletCode
     * 下载小程序码,返回的实际是文件,此处进行了转码处理 -> base64
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html#%E8%B0%83%E7%94%A8%E6%96%B9%E5%BC%8F">getUnlimitedQRCode</a>
     *
     * @param path        string	否	默认是主页，页面 page，例如 pages/index/index，根路径前不要填加 /，不能携带参数（参数请放在 scene 字段里），如果不填写这个字段，默认跳主页面。
     * @param envVersion  string	否	要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"。默认是正式版。
     * @param accessToken string	是	接口调用凭证，该参数为 URL 参数，非 Body 参数。使用getAccessToken 或者 authorizer_access_token
     * @return base64 图片
     */
    String getUnlimitedQRCode(String path, String envVersion, String accessToken);

    /**
     * 微信 /cgi-bin/ticket/getticket 接口中的 type 参数可以有以下几种取值：
     * <p>
     * jsapi：用于获取调用微信 JS-SDK 时所需的临时票据（jsapi_ticket）；
     * wx_card：用于获取调用微信卡券 API 时所需的临时票据（api_ticket）；
     * wx_card_mpnews：用于获取调用卡券图文消息群发接口时所需的临时票据（mpnews_ticket）；
     * wx_card_pay：用于获取调用微信支付接口时所需的临时票据（pay_ticket）。
     * 其中，jsapi 和 wx_card 是最常用的两种类型。
     *
     * <p><a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html#62">type=jsapi</a></p>
     * <p><a href="https://developers.weixin.qq.com/doc/offiaccount/WeChat_Invoice/Nontax_Bill/API_list.html#2.1%20%E8%8E%B7%E5%8F%96ticket">type=wx_card</a></p>
     *
     * @param type        type
     * @param accessToken accessToken
     * @return ticket
     */
    String getTicket(String type, String accessToken);

    /**
     * <p>
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html#62">附录1-JS-SDK使用权限签名算法</a>
     * </p>
     * <p>
     * 注意事项
     * 签名用的 noncestr 和timestamp必须与 wx.config 中的 nonceStr 和timestamp相同。
     * 签名用的 url 必须是调用 JS 接口页面的完整URL。
     * 出于安全考虑，开发者必须在服务器端实现签名的逻辑。
     * 如出现invalid signature 等错误详见附录5常见错误及解决办法。
     * </p>
     *
     * @param url    url
     * @param ticket ticket
     * @return R
     * @author zhouxin
     */
    Map<String, Object> getWxConfig(String url, String ticket);

    /**
     * <p>
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html#62">附录1-JS-SDK使用权限签名算法</a>
     * </p>
     * <p>
     * 注意事项
     * 签名用的 noncestr 和timestamp必须与 wx.config 中的 nonceStr 和timestamp相同。
     * 签名用的 url 必须是调用 JS 接口页面的完整URL。
     * 出于安全考虑，开发者必须在服务器端实现签名的逻辑。
     * 如出现invalid signature 等错误详见附录5常见错误及解决办法。
     * </p>
     *
     * @param url    url
     * @param ticket ticket
     * @return R
     * @author zhouxin
     */
    Map<String, Object> getWxConfig(String url, String ticket, String appId);

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/data-analysis/others/getVisitPage.html
     *
     * @param accessToken string	是	接口调用凭证，该参数为 URL 参数，非 Body 参数。使用access_token或者authorizer_access_token
     * @param beginDate   开始日期。格式为 yyyymmdd
     * @param endDate     结束日期，限定查询1天数据，允许设置的最大值为昨日。格式为 yyyymmdd
     * @return R
     */
    ArrayNode getVisitPage(String accessToken, LocalDate beginDate, LocalDate endDate);

    /**
     * 微信支付-商家-交易-应收|待收款|下单
     *
     * @return R
     */
    JsonNode transactionsReceivable();

    /**
     * 微信支付-商家-交易-应收|待收款|下单
     * 临时使用 后续不用 ,后续使用  com.zxxwl.user.service.api.wx.service.WxOpenApiService#transactionsReceivable()
     *
     * @return R
     */
    @Deprecated
    ObjectNode payUnifiedOrderV2Temp(String title, String orderSn, BigDecimal payAmount, Integer payType, String openid, String notify);

    @Deprecated
    JsonNode payUnifiedOrderV2(Map<String, String> map, String key);

    /**
     * 微信下单查询
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2">下单查询</a>
     *
     * @param appId      appId
     * @param nonceStr   随机字符串
     * @param outTradeNo 商家单号
     * @param mchId      mchId 微信商户id
     * @param key        密钥 key为商户平台设置的密钥key
     * @return R
     */
    @Deprecated
    JsonNode payOrderQueryV2(String appId, String mchId, String nonceStr, String outTradeNo, String key);

    /**
     * 微信下单查询
     *
     * @param map map
     * @param key 密钥 key为商户平台设置的密钥key
     * @return R
     */
    @Deprecated
    JsonNode payOrderQueryV2(Map<String, String> map, String key);

    /**
     * 微信订单 关闭
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_3">关闭订单</a>
     *
     * @param map map
     * @param key 密钥 key为商户平台设置的密钥key
     * @return R
     */
    @Deprecated
    JsonNode payOrderCloseV2(Map<String, String> map, String key);

    /**
     * 微信账单 退款查询
     *
     * @param map map
     * @param key 密钥 key为商户平台设置的密钥key
     * @return R
     */
    @Deprecated
    JsonNode payRefundQueryV2(Map<String, String> map, String key);

    /**
     * 获取用户基本信息（包括UnionID机制）
     *
     * @param accessToken 调用接口凭证
     * @param openid      普通用户的标识，对当前公众号唯一
     * @param lang        zh_CN 简体，zh_TW 繁体，en 英语
     * @return R
     * @apiNote 注此接口需 获取微信认证
     */

    JsonNode getUserInfo(String accessToken, String openid, String lang);
}
