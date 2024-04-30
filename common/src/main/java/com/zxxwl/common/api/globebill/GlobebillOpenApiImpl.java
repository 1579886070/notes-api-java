package com.zxxwl.common.api.globebill;

import com.zxxwl.common.utils.globebill.QBGlobeBillUtils;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class GlobebillOpenApiImpl implements GlobebillOpenApi {
    /**
     * 系统单位元 保留两位小数 ，钱宝支付 单位分，so: amount*100=tradeAmount
     */
    private static final long RATE = 100;
    private static final int EXPIRE_TIME = 1800;
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String KEY_SIGN_VALUE = "SignValue";
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    public JsonNode query(String orderSn) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("outTransId", orderSn)
//                .put("tradeId", "123420889563")
        ;
        return this.send(QBGlobeBillUtils.PATH_QUERY, bodyValue);
    }

    @Override
    public JsonNode queryByTradeId(String tradeId) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
//                .put("outTransId", orderSn)
                .put("tradeId", tradeId)
        ;
        return this.send(QBGlobeBillUtils.PATH_QUERY, bodyValue);
    }

    @Override
    public JsonNode pay(String orderSn, BigDecimal finalAmount, String notifyUrl, String appId, String userOpenId) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", QBGlobeBillUtils.SN)
                .put("payModeId", QBGlobeBillUtils.PAY_MODE_ID_WX_MINI)
                .put("tradeAmount", finalAmount.multiply(BigDecimal.valueOf(RATE)).intValue())
                // .put("merchantId",128255)
                // .put("payCode","")
                .put("outTransId", orderSn)
                .put("notifyUrl", notifyUrl)
                .put("expireTime", EXPIRE_TIME)
        ;
        if (Objects.nonNull(appId)) {
            bodyValue.put("appId", appId).put("userOpenId", userOpenId);
        }
        return this.send(QBGlobeBillUtils.PATH_PAY, bodyValue);
    }

    @Override
    public JsonNode payWxMini(String orderSn, BigDecimal finalAmount, String notifyUrl, String appId, String userOpenId) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", QBGlobeBillUtils.SN)
                .put("payModeId", QBGlobeBillUtils.PAY_MODE_ID_WX_MINI)
                .put("tradeAmount", finalAmount.multiply(BigDecimal.valueOf(RATE)).intValue())
                // .put("merchantId",128255)
                // .put("payCode","")
                .put("outTransId", orderSn)
                //.put("notifyUrl", notifyUrl)
                .put("appId", appId)
                .put("userOpenId", userOpenId)
                .put("expireTime", EXPIRE_TIME)
        ;
        if (StringUtils.hasText(notifyUrl)) {
            bodyValue.put("notifyUrl", notifyUrl);
        }
        JsonNode result = this.send(QBGlobeBillUtils.PATH_PAY, bodyValue);
        if (result.has("payInfo")) {
            try {
                ObjectNode res = objectMapper.convertValue(result, ObjectNode.class);
                res.putPOJO("payInfo", objectMapper.readTree(result.path("payInfo").asText()));
                return res;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.error("fail result:{},api:{}", result, "");
            throw new CustomThirdPartyApiException("", "请求失败:" + result, 500);
        }
    }

    @Override
    public JsonNode refund(String orderSn, BigDecimal amount) {

        return refund(null, orderSn, amount);
    }

    @Override
    public JsonNode refund(String refundSn, String orderSn, BigDecimal amount) {
        // 退款表主键
        String tradeRemark = "tradeRemark";
        // String sourceTradeId = "sourceTradeId";
        // String sourceChannelTransNO = "sourceChannelTransNO";

        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", QBGlobeBillUtils.SN)
                .put("tradeAmount", amount.doubleValue() * 100)
                // .put("merchantId",128255)
                .put("sourceOutTransId", orderSn)
                // .put("sourceTradeId", sourceTradeId)
                // .put("sourceChannelTransNO", sourceChannelTransNO)
                .put("outTransId", refundSn)
//                .put("notifyUrl", notifyUrl)
                .put("tradeRemark", tradeRemark)
        ;
        return this.send(QBGlobeBillUtils.PATH_REFUND, bodyValue);
    }

    @Override
    public JsonNode notify(JsonNode body) {
        if (STATUS_SUCCESS.equals(body.path("tradeStatus").asText(""))) {
            // 支付成功
        }
        return null;
    }

    /**
     * 响应格式
     * <p>
     * code	响应码
     * msg	响应消息
     * result	业务结果，JSON格式(可能为null)
     * </p>
     * 响应码
     * <p>
     * SUCCESS	成功
     * ERROR_URL	请求地址错误
     * ERROR_PARAMETER	请求参数错误
     * ERROR_SIGN	签名错误
     * ERROR_DATA	数据错误
     * ERROR_SERVICE	服务异常
     * ERROR_NETWORK	网络错误
     * ERROR_DEVICE	设备错误
     * TRADE_FAILURE	交易操作失败
     * TRADE_NOTFOUND	交易不存在
     * </p>
     */
    @Override
    public JsonNode send(String apiPath, JsonNode bodyValue) {
        String url = UriComponentsBuilder.fromUriString(QBGlobeBillUtils.SERVER)
                .path(apiPath)
                .build()
                .toString();

        Map<String, String> httpHeadersMap = this.signHeaders(bodyValue);

        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.setAll(httpHeadersMap))
                    .bodyValue(bodyValue)
                    .exchangeToMono(resp ->
                            resp.bodyToMono(ByteBuffer.class).mapNotNull(respStr -> {
                                JsonNode reqpJsonNode;
                                try {
                                    reqpJsonNode = objectMapper.readTree(respStr.array());
                                    if (STATUS_SUCCESS.equals(reqpJsonNode.path("code").asText(""))) {
                                        List<String> signValue = resp.headers().header(KEY_SIGN_VALUE);
                                        String sigValRsp = !signValue.isEmpty() ? signValue.get(0) : null;
                                        if (StringUtils.hasText(sigValRsp)) {
                                            boolean verify = QBGlobeBillUtils.verify256(respStr, sigValRsp, QBGlobeBillUtils.GLOBEBILL_PUBLIC_KEY);
                                            log.info("验签结果：{}", verify);
                                        }
                                    }
                                } catch (IOException e) {
                                    log.error("convert error");
                                    throw new RuntimeException(e);
                                }
                                return reqpJsonNode;
                            })
                    )
                    .onErrorResume(throwable -> {
                        log.error("api:{}, client:{} ", url, throwable.getMessage());
                        if (throwable instanceof WebClientResponseException ex) {
                            log.error("ex.getStatusCode():{}", ex.getStatusCode());
                            if (ex.getStatusCode().is4xxClientError()) {
                                return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Client Error", ex.getStatusCode().value()));
                            } else if (ex.getStatusCode().is5xxServerError()) {
                                return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Server Error", ex.getStatusCode().value()));
                            }
                        }
                        return Mono.error(new CustomThirdPartyApiException(throwable, url, "ThirdParty  Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
                    });

            result = voidMono.block();
            log.info("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, "", "请求失败", 500);
        }
        assert result != null;
        if (STATUS_SUCCESS.equals(result.path("code").asText())) {
            return result.path("result");
        } else if (result.path("result").isEmpty()) {
            // 失败返回空
            return NullNode.getInstance();
        } else {
            log.error("fail result:{},bodyValue:{}", result, bodyValue);
            throw new CustomThirdPartyApiException("", "请求失败:" + result, 500);
        }
//        return result;
    }

    private Map<String, String> signHeaders(JsonNode bodyValue) {
        // SHA256withRSA签名
        return Map.of(
                "AccessId", QBGlobeBillUtils.ACCESS_ID,
                "SignType", QBGlobeBillUtils.SIGNATURE_ALGORITHM,
                "SignValue", QBGlobeBillUtils.sign256(bodyValue.toString(), QBGlobeBillUtils.MERCHANT_PRIVATE_KEY),
                "Timestamp", QBGlobeBillUtils.getTimestamp()
        );
    }
}
