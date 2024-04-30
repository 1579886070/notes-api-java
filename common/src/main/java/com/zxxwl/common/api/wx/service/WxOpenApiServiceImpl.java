package com.zxxwl.common.api.wx.service;


import com.zxxwl.common.constants.WxConstants;
import com.zxxwl.common.constants.WxEmp;
import com.zxxwl.common.random.IdUtils;
import com.zxxwl.common.utils.http.WebClientUtil;
import com.zxxwl.common.utils.wx.WxUtils;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.zxxwl.common.constants.WxConstants.*;

/**
 * 建议加入统一异常处理
 *
 * @author qingyu
 * @author yufuqing
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class WxOpenApiServiceImpl implements WxOpenApiService {
    /**
     * 微信回调
     */
//    @Value("${payment.petCard.wxPayNotify}")
//    public String wxPayNotify;
    private final WxOpenApiCache wxOpenApiCache;
    private final WebClientUtil webClientUtil;
    private final ObjectMapper objectMapper;


    @Override
    public String getAccessToken(String appId, String secret) {
        String accessToken = wxOpenApiCache.getAccessToken(appId);
        if (!StringUtils.hasText(accessToken)) {
            accessToken = upAndCacheAccessToken(appId, secret);
        }
        return accessToken;
    }


    @Override
    public JsonNode getOAuth2(String appId, String secret, String code) {
        String url = UriComponentsBuilder.fromUriString(WxConstants.BASE_URL)
                .path(SNS_AUTH2_ACCESS_TOKEN)
                .queryParam("appid", appId)
                .queryParam("secret", secret)
                .queryParam("code", code)
                .queryParam("grant_type", GRANT_TYPE_AUTHORIZATION_CODE)
                .build()
                .toString();

        JsonNode result = null;
        try {
            result = webClientUtil.get(url);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, SNS_AUTH2_ACCESS_TOKEN, "请求失败", 500);
        }
        if (result.has("access_token")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, SNS_AUTH2_ACCESS_TOKEN);
            throw new CustomThirdPartyApiException(SNS_AUTH2_ACCESS_TOKEN, "请求失败:" + result, 500);
        }
    }


    @Override
    public JsonNode code2Session(String appId, String secret, String jsCode) {
        String url = UriComponentsBuilder.fromUriString(WxConstants.BASE_URL)
                .path(WxConstants.SNS_CODE2_SESSION)
                .queryParam("appid", appId)
                .queryParam("secret", secret)
                .queryParam("js_code", jsCode)
                .queryParam("grant_type", GRANT_TYPE_AUTHORIZATION_CODE)
                .build()
                .toString();

        JsonNode result = null;
        try {
            // FIXME 2023.02.11 目前 "/sns/jscode2session"  返回 Content-Type:text/plain
            // result = webClientUtil.get(url);
            result = webClientUtil.getJsonStrToJson(url);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, SNS_AUTH2_ACCESS_TOKEN, "请求失败", 500);
        }
        if (result.has("openid")) {
            // 数据处理 去除无用属性
            return objectMapper.createObjectNode()
                    .put("openid", result.path("openid").asText())
                    .put("unionid", result.path("unionid").asText())
                    .put("session_key", result.path("session_key").asText());
        } else {
            log.error("fail result:{},api:{},url:{}", result, SNS_AUTH2_ACCESS_TOKEN,url);
            throw new CustomThirdPartyApiException(SNS_AUTH2_ACCESS_TOKEN, "请求失败:" + result, 500);
        }
    }



    @Override
    public JsonNode getUserInfoByOAuth2(String accessToken, String openid, String lang) {
        String url = UriComponentsBuilder.fromUriString(WxConstants.BASE_URL)
                .path(SNS_USERINFO)
                .queryParam("access_token", accessToken)
                .queryParam("openid", openid)
                .queryParam("lang", lang)
                .build()
                .toString();
        JsonNode result = null;
        try {
            result = webClientUtil.get(url);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, SNS_USERINFO, "请求失败", 500);
        }
        if (result.has("openid")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, SNS_USERINFO);
            throw new CustomThirdPartyApiException(SNS_USERINFO, "请求失败:" + result, 500);
        }
    }

    /**
     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     *
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openid      用户的唯一标识
     * @return R
     */

    @Override
    public String ckOAuth2(String accessToken, String openid) {
        // TODO
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .path(WxConstants.SNS_AUTH)
                .queryParam("access_token", accessToken)
                .queryParam("openid", openid)
                .build()
                .toString();
        JsonNode result = webClientUtil.get(url);
        return result.toString();
    }


    @SneakyThrows
    @Override
    public String upAndCacheAccessToken(String appId, String secret) {
        JsonNode newAccessToken = getNewAccessToken2(appId, secret);
        String accessToken = newAccessToken.path("access_token").asText();
        int expiresIn = newAccessToken.path("expires_in").asInt();
        wxOpenApiCache.setAccessToken(accessToken, expiresIn, appId);
        return accessToken;
    }

    @Deprecated
    private String getNewAccessToken(String appid, String secret) {
        String url = UriComponentsBuilder.fromUriString(WxConstants.BASE_URL)
                .path(WxConstants.CGI_BIN_TOKEN)
                .queryParam("appid", appid)
                .queryParam("secret", secret)
                .queryParam("grant_type", GRANT_TYPE_CLIENT_CREDENTIAL)
                .build()
                .toString();
        JsonNode result = webClientUtil.get(url);
        log.debug("{}", result.toString());
        return result.toString();
    }

    private JsonNode getNewAccessToken2(String appId, String secret) {
        String url = UriComponentsBuilder.fromUriString(WxConstants.BASE_URL)
                .path(WxConstants.CGI_BIN_TOKEN)
                .queryParam("appid", appId)
                .queryParam("secret", secret)
                .queryParam("grant_type", GRANT_TYPE_CLIENT_CREDENTIAL)
                .build()
                .toString();
        JsonNode result = null;
        try {
            result = webClientUtil.get(url);
        } catch (Exception e) {
            log.error("getNewAccessToken2:APi:{},Error:{}", CGI_BIN_TOKEN, e);
            throw new CustomThirdPartyApiException(e, CGI_BIN_TOKEN, "请求失败", 500);
        }
        if (result.has("access_token")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, CGI_BIN_TOKEN);
            throw new CustomThirdPartyApiException(CGI_BIN_TOKEN, "请求失败:" + result, 500);
        }
    }

    /**
     * api success result: {@code  {"errcode":0,"errmsg":"ok"}}
     */
    @Override
    public JsonNode sendMessage(ObjectNode bodyValue, String accessToken) {
        String url = getUrlWithToken(CGI_BIN_MSG_SUBSCRIBE_SEND, accessToken);
        JsonNode result = null;
        try {
            result = webClientUtil.post(url, bodyValue);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, CGI_BIN_MSG_SUBSCRIBE_SEND, "请求失败", 500);
        }
        if (isOkResult(result)) {
            log.debug("msg:{}", result);
            return result;
        } else {
            log.error("fail result:{},api:{}", result, CGI_BIN_MSG_SUBSCRIBE_SEND);
            throw new CustomThirdPartyApiException(CGI_BIN_MSG_SUBSCRIBE_SEND, "请求失败:" + result, 500);
        }
    }

    /**
     * {@code {
     * "errcode":0,
     * "errmsg":"ok",
     * "phone_info": {
     * "phoneNumber":"xxxxxx",
     * "purePhoneNumber": "xxxxxx",
     * "countryCode": 86,
     * "watermark": {
     * "timestamp": 1637744274,
     * "appid": "xxxx"
     * }
     * }
     * }}
     */
    @Override
    public JsonNode getPhoneByCode(String code, String accessToken) {
        String url = getUrlWithToken(WXA_PHONE_NUMBER, accessToken);
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("code", code);
        JsonNode result = null;
        try {
            result = webClientUtil.post(url, bodyValue);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, WXA_PHONE_NUMBER, "请求失败", 500);
        }
        if (isOkResult(result)) {
            return result.path("phone_info");
        } else {
            log.error("fail result:{},api:{},body:{}", result, WXA_PHONE_NUMBER,bodyValue);
            throw new CustomThirdPartyApiException(WXA_PHONE_NUMBER, "请求失败:" + result, 500);
        }
    }

    /**
     * {@code {
     * "errcode": 0,
     * "errmsg": "ok",
     * "contentType": "image/jpeg",
     * "buffer": Buffer
     * } }
     */
    @Override
    public String getUnlimitedQRCode(String path, String envVersion, String accessToken) {
        String url = getUrlWithToken(WXA_UN_LIMIT_CODE, accessToken);
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("path", path)
                .put("env_version", envVersion)
                .put("scene", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli())
                .put("check_path", false)
                .put("is_hyaline", true)
        ;
        ByteBuffer resource = null;
        String encoded = null;
        try {
            resource = webClientUtil.postFileAsBuffer(url, bodyValue);
            encoded = Base64.getEncoder().encodeToString(resource.array());
            log.debug("getUnlimitedQRCode preview(result no prefix 'data:image/png;base64,'):\ndata:image/png;base64,{}", encoded);
        } catch (Exception e) {
            log.error("getUnlimitedQRCode Error:{}", e.getMessage());
            throw new CustomThirdPartyApiException(e, WXA_UN_LIMIT_CODE, "请求失败", 500);
        }
        return encoded;
    }

    /**
     * {@code {
     * "errcode": 0,
     * "errmsg": "ok",
     * "ticket": "m7RQzjA_ljjEkt-JCoklRM5zrzYr-6PI09QydZmNXXz-opTqMv53aFj1ykRt_AOtvqidqZZsLhCDgwGC6nBDiA",
     * "expires_in": 7200
     * }}
     */
    @Override
    public String getTicket(String type, String accessToken) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .path(CGI_BIN_TICKET_GET)
                .queryParam("access_token", accessToken)
                .queryParam("type", type)
                .build()
                .toString();
        JsonNode result = null;
        try {
            result = webClientUtil.get(url);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, WXA_UN_LIMIT_CODE, "请求失败", 500);
        }
        if (isOkResult(result)) {
            return result.path("ticket").asText();
        } else {
            log.error("fail result:{},api:{}", result, WXA_UN_LIMIT_CODE);
            throw new CustomThirdPartyApiException(WXA_UN_LIMIT_CODE, "请求失败:" + result, 500);
        }
    }


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
    @Override
    public Map<String, Object> getWxConfig(String url, String ticket) {
        Map<String, Object> result = new HashMap<>();

//        String appId = WX_PUBLIC_API_ID; // 必填，公众号的唯一标识
        return getWxConfig(url, ticket, WX_PUBLIC_API_ID);
    }

    @Override
    public Map<String, Object> getWxConfig(String url, String ticket, String appId) {
        Map<String, Object> result = new HashMap<>();

//        String appId = WX_PUBLIC_API_ID; // 必填，公众号的唯一标识
//        String secret = WX_PUBLIC_SECRET;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000); // 必填，生成签名的时间戳
        String nonceStr = IdUtils.fastSimpleUUID();

        String sign = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;

        String signature = WxUtils.getSignature(sign);

        result.put("appId", appId);
        result.put("timestamp", timestamp);
        result.put("nonceStr", nonceStr);
        result.put("signature", signature);

        return result;
    }

    @Override
    public ArrayNode getVisitPage(String accessToken, LocalDate beginDate, LocalDate endDate) {
        String url = getUrlWithToken(DATACUBE_VISITPAGE, accessToken);
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("begin_date", beginDate.format(DateTimeFormatter.ofPattern(API_DATE_FORMAT)));
        bodyValue.put("end_date", endDate.format(DateTimeFormatter.ofPattern(API_DATE_FORMAT)));
        JsonNode result = null;
        try {
            result = webClientUtil.post(url, bodyValue);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, DATACUBE_VISITPAGE, "请求失败", 500);
        }
        if (result.has("list")) {
            return result.withArray("list");
        } else {
            log.error("fail result:{},api:{}", result, DATACUBE_VISITPAGE);
            throw new CustomThirdPartyApiException(DATACUBE_VISITPAGE, "请求失败:" + result, 500);
        }
    }

    /**
     * 校验 是否请求成功|是否成功获取期望
     *
     * @param result result
     * @return ok
     */
    private static boolean isOkResult(JsonNode result) {
        return result.path("errcode").asInt(-1) == 0;
    }

    /**
     * 获取 url with accessToken
     *
     * @param api         api
     * @param accessToken accessToken
     * @return getUrlWithToken
     */
    public String getUrlWithToken(String api, String accessToken) {
        return UriComponentsBuilder.fromUriString(WxEmp.SERVICE_URL)
                .path(api)
                .queryParam("access_token", accessToken)
                .build()
                .toString();
    }

    /*  private String getUrl(String api) {
          String accessToken = getAccessTokenByCode();
          return String.format("%s/%s?access_token=%s", WxEmp.SERVICE_URL, api, accessToken);
      }
      */
    @Deprecated
    public String getUrlWithToken(String api, String appid, String secret) {
        String accessToken = this.getAccessToken(appid, secret);
        return String.format("%s/%s?access_token=%s", WxEmp.SERVICE_URL, api, accessToken);
    }

    @Deprecated
    public String getUrl2(String api, String appid, String secret) {
        String accessToken = this.getAccessToken(appid, secret);
        return UriComponentsBuilder.fromUriString(WxEmp.SERVICE_URL)
                .path(api)
                .queryParam("access_token", accessToken)
                .build()
                .toString();
    }

    @Override
    public JsonNode transactionsReceivable() {
        String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(JSAPI_RECEIVABLE)
                .build()
                .toString();
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("appid", "");
        bodyValue.put("mch_id", "");
        bodyValue.put("nonce_str", "");
        bodyValue.put("attach", "");
        bodyValue.put("notify_url", "");
        bodyValue.put("body", "");
        bodyValue.put("out_trade_no", "");
        ObjectNode amount = objectMapper.createObjectNode();
        // 总金额 单位分
        amount.put("total", "");
        amount.put("currency", "CNY");
        bodyValue.putIfAbsent("amount", amount);
        ObjectNode payer = objectMapper.createObjectNode();
        // 支付用户OpenId
        payer.put("openid", "");
        bodyValue.putIfAbsent("payer", payer);
        bodyValue.put("time_expire", "");
        JsonNode result = null;
        try {
            result = webClientUtil.post(url, bodyValue);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        if (result.has("prepay_id")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, JSAPI_RECEIVABLE);
            throw new CustomThirdPartyApiException(JSAPI_RECEIVABLE, "请求失败:" + result, 500);
        }
    }


    @Override
    public ObjectNode payUnifiedOrderV2Temp(String title, String orderSn, BigDecimal payAmount, Integer payType, String openid, String notify) {
        /*Map<String, Object> payOrder = WxPaySignUtil.getPayOrderInfo(title,
                orderSn,
                payAmount.multiply(new BigDecimal(100)).longValue(),
                notify,
                payType,
                openid,
                "遛遛它");
        ObjectNode payResult = objectMapper.createObjectNode();
        payResult.put("orderSn", orderSn);
        payResult.put("orderId", orderSn);
        if (payOrder == null) {
            payResult.put("code", 500);
            payResult.put("msg", "生成微信支付对象失败！");
            return payResult;
        }
        log.debug("payUnifiedOrderV2Temp debug:{}", payOrder);
        Map wxMap = (Map) payOrder.get("wx");
        if (wxMap == null) {
            payResult.put("code", 500);
            payResult.put("msg", "生成微信支付对象失败[wx]！");
            return payResult;
        }

        if ((wxMap.get("return_code").toString()).equals("SUCCESS")) {
            // 如果支付状态正确
            try {
                payResult.put("appId", wxMap.get("appid").toString());
                payResult.put("partnerId", wxMap.get("mch_id").toString());
                payResult.put("prepayId", wxMap.get("prepay_id").toString());
                payResult.put("nonceStr", wxMap.get("nonce_str").toString());
                payResult.put("timeStamp", wxMap.get("timestamp").toString());
                payResult.put("sign", wxMap.get("sign").toString());

                if (payType.equals(MemberConstants.WEI_XIN_ACCOUNT_TYPE)) {
                    payResult.put("packageValue", "Sign=WXPay");
                } else {
                    payResult.put("packageValue", "prepay_id=" + wxMap.get("prepay_id").toString());
                }
            } catch (Exception e) {
                log.error("【微信生成支付对象使用时出错：[{}]】", e.getMessage());
            }
            payResult.put("code", 0);
            payResult.put("msg", "成功！");
            return payResult;
        } else {
            log.error("生成微信支付对象状态不正确！:{}", payOrder);
            payResult.put("code", 500);
            payResult.put("msg", "生成微信支付对象状态不正确！");
            return payResult;
        }*/
        return null;
    }

    @Override
    public JsonNode payUnifiedOrderV2(Map<String, String> map, String key) {
        /*String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(V2_PAY_UNIFIED_ORDER)
                .build()
                .toString();

        JsonNode resp = null;
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, key, WXPayConstants.SignType.MD5);
            log.debug("{}", signedXml);
            resp = webClientUtil.postXmlResultJson(url, signedXml);
            log.debug("{}", resp);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        return resp;*/
        return null;
    }

    @SneakyThrows
    @Override
    public JsonNode payOrderQueryV2(String appId, String mchId, String nonceStr, String outTradeNo, String key) {
        /*String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(JSAPI_V2_PAY_ORDER_QUERY)
                .build()
                .toString();
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("mch_id", mchId);
        map.put("nonce_str", nonceStr);
        map.put("out_trade_no", outTradeNo);
        JsonNode resp = null;
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, key, WXPayConstants.SignType.MD5);
            log.debug("{}", signedXml);
            resp = webClientUtil.postXmlResultJson(url, signedXml);
            log.debug("{}", resp);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        return resp;*/
        return null;
    }

    @Override
    public JsonNode payOrderQueryV2(Map<String, String> map, String key) {
        /*String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(JSAPI_V2_PAY_ORDER_QUERY)
                .build()
                .toString();
        JsonNode resp = null;
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, key, WXPayConstants.SignType.MD5);
            log.debug("{}", signedXml);
            resp = webClientUtil.postXmlResultJson(url, signedXml);
            log.debug("{}", resp);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        return resp;*/
        return null;
    }

    @Override
    public JsonNode payOrderCloseV2(Map<String, String> map, String key) {
        /*String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(JSAPI_V2_PAY_ORDER_CLOSE)
                .build()
                .toString();
        JsonNode resp = null;
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, key, WXPayConstants.SignType.MD5);
            log.debug("{}", signedXml);
            resp = webClientUtil.postXmlResultJson(url, signedXml);
            log.debug("{}", resp);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        return resp;*/
        return null;
    }

    @Override
    public JsonNode payRefundQueryV2(Map<String, String> map, String key) {
        /*String url = UriComponentsBuilder.fromUriString(MCH_BASE_URL)
                .path(JSAPI_V2_PAY_REFUND_QUERY)
                .build()
                .toString();
        JsonNode resp = null;
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, key, WXPayConstants.SignType.MD5);
            log.debug("{}", signedXml);
            resp = webClientUtil.postXmlResultJson(url, signedXml);
            log.debug("{}", resp);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, JSAPI_RECEIVABLE, "请求失败", 500);
        }
        return resp;*/
        return null;
    }

    @Override
    public JsonNode getUserInfo(String accessToken, String openid, String lang) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .path(CGI_BIN_USER_INFO)
                .queryParam("access_token", accessToken)
                .queryParam("openid", openid)
                .queryParam("lang", lang)
                .build()
                .toString();
        JsonNode result = null;
        try {
            result = webClientUtil.get(url);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, SNS_USERINFO, "请求失败", 500);
        }
        if (result.has("subscribe")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, SNS_USERINFO);
            throw new CustomThirdPartyApiException(SNS_USERINFO, "请求失败:" + result, 500);
        }
    }
}
