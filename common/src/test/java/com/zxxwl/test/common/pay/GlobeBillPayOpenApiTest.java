package com.zxxwl.test.common.pay;

import com.alibaba.fastjson.JSONObject;
import com.zxxwl.common.utils.globebill.QBGlobeBillUtils;
import com.zxxwl.config.JsonConfig;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用API遵循以下规则
 * 传输方式	采用 HTTP/HTTPS 传输
 * 提交方式	采用 POST 方法提交
 * 数据格式	提交和返回数据都为 JSON 格式
 * 字符编码	统一采用 UTF-8 字符编码
 * 签名算法	目前支持 SHA256withRSA
 * 签名要求	请求和接收数据均需要校验签名
 * 时间容差	请求时间与服务器接收时间相差不能超过2分钟
 * HTTP HEADER 公共参数
 * AccessId	接入商编号
 * Timestamp	请求发送时间，格式：yyyy-MM-dd HH:mm:ss，北京时间
 * SignType	签名算法
 * SignValue	签名值
 */
@Slf4j
public class GlobeBillPayOpenApiTest {
    public ObjectMapper objectMapper = JsonConfig.getInstance();
    WebClient webClient = WebClient.builder().build();


    @Test
    public void queryDemo() {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("outTransId", "GBDEMO20230316170401");
        JsonNode pay = payApiDemo(QBGlobeBillUtils.PATH_QUERY, bodyValue);
//        JsonNode pay = payApiDemoV1(QBGlobeBillUtils.PATH_QUERY, bodyValue);
        log.info("{}", pay);
    }

    @Test
    public void payDemo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", "222NBC906777")
                .put("tradeAmount", 1)
                .put("payModeId", 10042)
                .put("outTransId", "GBDEMO" + timestamp)
                .put("payCode", "280254649279871543")
        ;
        JsonNode pay = payApiDemo(QBGlobeBillUtils.PATH_PAY, bodyValue);
        log.info("{}", pay);
    }

    @Test
    public void queryTest() {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
//                .put("tradeId", "123420754560")
//                .put("outTransId", "null")
//                .put("outTransId", "GBDEMO20230912163441")
//                .put("outTransId", "GBDEMO20230913130842")
//                .put("outTransId", "GBDEMO20230913173501")
//                .put("tradeId", "123420934707")
//                .put("tradeId", "123420889563")
                .put("tradeId", "123420934707")
        ;
        JsonNode pay = payApi(QBGlobeBillUtils.PATH_QUERY, bodyValue);
        log.info("{}", pay);
    }

    @Test
    public void payTest() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", QBGlobeBillUtils.SN)
                .put("tradeAmount", 1)
                .put("payModeId", QBGlobeBillUtils.PAY_MODE_ID_WX_MINI)
//                .put("merchantId",128255)
//                .put("payCode","")
                .put("outTransId", "GBDEMO" + timestamp)
                /*.put("appId", "wxf85576319c33dd1b")
                .put("userOpenId", "o5Uuo4iS5rLXul-LqtG5QvmYVPXE")*/
                .put("appId", "wx88b6c385020f8831")
                .put("userOpenId", "oX4KK65lrmFYLDrOpT6B3jCcrW-U")
        ;
        JsonNode pay = payApi(QBGlobeBillUtils.PATH_PAY, bodyValue);
        log.info("{}", pay);
    }

    /**
     * 支付宝刷卡支付
     */
    @Test
    public void payTest2() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue
                .put("sn", QBGlobeBillUtils.SN)
                .put("tradeAmount", 1)
                .put("payModeId", 10042)
                .put("outTransId", "GBDEMO" + timestamp)
                .put("payCode", "280254649279871544")
        ;
        JsonNode pay = payApi(QBGlobeBillUtils.PATH_PAY, bodyValue);
        log.info("{}", pay);
    }

    public JsonNode payApi(String apiPath, JsonNode bodyValue) {
        String url = UriComponentsBuilder.fromUriString(QBGlobeBillUtils.SERVER)
                .path(apiPath)
                .build()
                .toString();
        // SHA256withRSA签名
        String signVal = QBGlobeBillUtils.sign256(bodyValue.toString(), QBGlobeBillUtils.MERCHANT_PRIVATE_KEY);
        // 时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, String> httpHeadersMap = Map.of(
                "AccessId", QBGlobeBillUtils.ACCESS_ID,
                "SignType", QBGlobeBillUtils.SIGNATURE_ALGORITHM,
                "SignValue", signVal,
                "Timestamp", timestamp
        );
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
                                    if ("SUCCESS".equals(reqpJsonNode.path("code").asText(""))) {
                                        List<String> signValue = resp.headers().header("SignValue");
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
//                        log.error("api:{}, client:{} ", url, throwable.getMessage());
                        log.error("api:{}, error:", url, throwable);
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
        if ("SUCCESS".equals(result.path("code").asText() )) {
            return result.path("data");
        } else {
            log.error("fail result:{},api:{}", result, "");
            throw new CustomThirdPartyApiException("", "请求失败:" + result, 500);
        }
//        return result;
    }
    /*public JsonNode payApi(String apiPath, JsonNode bodyValue) {
        String url = UriComponentsBuilder.fromUriString(QBGlobeBillUtils.SERVER)
                .path(apiPath)
                .build()
                .toString();
//        ObjectNode bodyValue = objectMapper.createObjectNode();

        String signVal = QBGlobeBillUtils.sign256(bodyValue.toString(), QBGlobeBillUtils.DEV_PRIVATE_KEY);//SHA256withRSA签名

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> {
                                httpHeaders.add("AccessId", QBGlobeBillUtils.ACCESS_ID);
                                httpHeaders.add("SignValue", signVal);
                                httpHeaders.add("SignType", QBGlobeBillUtils.SIGNATURE_ALGORITHM);
                                httpHeaders.add("Timestamp", timestamp);
                            }
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(bodyValue)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .onErrorResume(throwable -> {
                        log.error("api:{}, client:{} ", url, throwable.getMessage());
                        if (throwable instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) throwable;
                            if (ex.getStatusCode().is4xxClientError()) {
                                return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Client Error", ex.getStatusCode().value()));
                            } else if (ex.getStatusCode().is5xxServerError()) {
                                return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Server Error", ex.getStatusCode().value()));
                            }
                        }
                        return Mono.error(new CustomThirdPartyApiException(throwable, url, "ThirdParty  Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
                    });
            result = voidMono.block();
//            log.debug("rest:{}", result);
            log.info("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, "", "请求失败", 500);
        }
        *//*if (result.path("code").asInt() == 200) {
            return result.path("data");
        } else {
            log.error("fail result:{},api:{}", result, "");
            throw new CustomThirdPartyApiException("", "请求失败:" + result, 500);
        }*//*
        return result;
    }*/


    public JsonNode payApiDemo(String apiPath, JsonNode bodyValue) {
        String url = UriComponentsBuilder.fromUriString(QBGlobeBillUtils.SERVER_TEST)
                .path(apiPath)
                .build()
                .toString();

        String signVal = QBGlobeBillUtils.sign256(bodyValue.toString(), QBGlobeBillUtils.DEV_PRIVATE_KEY_TEST);//SHA256withRSA签名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, String> httpHeadersMap = Map.of(
                "AccessId", QBGlobeBillUtils.ACCESS_ID_TEST,
                "SignType", QBGlobeBillUtils.SIGNATURE_ALGORITHM,
                "SignValue", signVal,
                "Timestamp", timestamp
        );
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.setAll(httpHeadersMap))
                    .bodyValue(bodyValue)
                    .exchangeToMono(resp -> {
                        List<String> signValue = resp.headers().header("SignValue");
                        String sigValRsp = !signValue.isEmpty() ? signValue.get(0) : null;
                        return resp.bodyToMono(ByteBuffer.class).mapNotNull(respStr -> {
                            if (StringUtils.hasText(sigValRsp)) {
                                boolean verify = QBGlobeBillUtils.verify256(respStr, sigValRsp, QBGlobeBillUtils.QB_PUBLIC_KEY_TEST);
                                log.info("验签结果：{}", verify);
                            }
                            try {
                                return objectMapper.readTree(respStr.array());
                            } catch (IOException e) {
                                log.error("convert error");
                                throw new RuntimeException(e);
                            }
                        });
                    })
                    .onErrorResume(throwable -> {
                        log.error("api:{}, client:{} ", url, throwable.getMessage());
                        if (throwable instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) throwable;
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
        /*if (result.path("code").asInt() == 200) {
            return result.path("data");
        } else {
            log.error("fail result:{},api:{}", result, "");
            throw new CustomThirdPartyApiException("", "请求失败:" + result, 500);
        }*/
        return result;
    }


    private static Map<String, String> postAndVerify(JSONObject jo, String api) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String json = jo.toJSONString();
        String signVal = Utils.sign256(json, Utils.DevPrivateKey);//SHA256withRSA签名
        log.info("data：{}", json);
        log.info("signVal：{}", signVal);
        String url = Utils.server + api;
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> responseHeaders = new HashMap<>();
        String timestamp = sdf2.format(new Date());
        log.info("signVal：{}", timestamp);

        headers.put("AccessId", "3011");
        headers.put("Timestamp", timestamp);
        headers.put("SignType", Utils.SIGNATURE_ALGORITHM);
        headers.put("SignValue", signVal);
        String respStr = Utils.httpPost(url, json, headers, responseHeaders);
//        System.out.println(responseHeaders.toString());
        String sigValRsp = responseHeaders.get("SignValue");

        if (sigValRsp != null && sigValRsp.length() > 0) {
            /*boolean verify = Utils.verify256(respStr, sigValRsp, Utils.QBPublicKey);
            log.info("signValue:{}", sigValRsp);
            log.info("respStr:{}", respStr);
            System.out.println("验签结果：" + verify);*/
            log.info("signValue:\n{}", sigValRsp);
            log.info("respStr:\n{}", respStr);
            boolean verify = QBGlobeBillUtils.verify256(respStr, sigValRsp, QBGlobeBillUtils.QB_PUBLIC_KEY_TEST);
            boolean verify2 = Utils.verify256(respStr, sigValRsp, QBGlobeBillUtils.QB_PUBLIC_KEY_TEST);
            boolean verify3 = Utils.verify256(respStr, sigValRsp, Utils.QBPublicKey);
            log.info("gb-验签结果：{}", verify);
            log.info("gb-验签结果verify2：{}", verify2);
            log.info("gb-verify3：{}", verify3);
        }

        Map<String, String> map = new HashMap<>();
        map.put("respStr", respStr);
        map.put("sigValRsp", sigValRsp);
        return map;
    }

    private static void payTestG() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        JSONObject jo = new JSONObject();
        jo.put("sn", "222NBC906777");
        jo.put("tradeAmount", 1);
        jo.put("payModeId", 10042);
        jo.put("outTransId", "GBDEMO" + sdf1.format(new Date()));
        jo.put("payCode", "280254649279871542");
        //jo.put("notifyUrl", "");
        String api = "/qrcode/pay";
        postAndVerify(jo, api);
    }

    private static Map<String, String> queryTestG() {
        JSONObject jo = new JSONObject();
        jo.put("outTransId", "GBDEMO20230316170401");
        //jo.put("tradeId", "123400014302");
        //jo.put("appAccessId", 0);//必须为数字，否则接口可能报错
        String api = "/qrcode/query";
        Map<String, String> stringStringMap = postAndVerify(jo, api);
        return stringStringMap;
    }
}
