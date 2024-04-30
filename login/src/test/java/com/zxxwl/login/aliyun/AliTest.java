package com.zxxwl.login.aliyun;

import com.zxxwl.config.JsonConfig;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
public class AliTest {
    /**
     * 阿里云身份认证
     */
    // static final String ID_CARD_APPCODE = "2afea7f3a94f4456add323c7755315c2";
    //public static final String ID_CARD_HOST = "https://jmidcardv1.market.alicloudapi.com";

    public static final String ID_CARD_APPCODE = "d445712699e84f34bd56a765c11a8b26";
    public static final String ID_CARD_HOST = "https://jmidcardv1.market.alicloudapi.com";

    public static final String ID_CARD_PATH = "/idcard/validate";
    public ObjectMapper objectMapper = JsonConfig.getInstance();
    WebClient webClient = WebClient.builder().build();

    /**
     * {"data":{},"msg":"请输入有效的身份证号码","success":false,"code":400,"charge":false}
     * {"data":{"result":1,"desc":"不一致","sex":"男","birthday":"20010109","address":"河南省周口地区项城市"},"msg":"成功","success":true,"code":200,"taskNo":"159373647215074770465673"}
     */
    @Test
    public void t() {
//        JsonNode jsonNode = idCardValidate("412702200101096510", "于富清");
//        JsonNode jsonNode = idCardValidate("412702200101096511", "于富");
        JsonNode jsonNode = idCardValidate("412702200101096511", "于富清");
        if (jsonNode.path("data").isEmpty()) {
            log.info("{}", jsonNode.path("msg").asText());
        } else {
            int result = jsonNode.path("data").path("result").asInt();
            if (result == 0) {
                log.info("ok");

            } else {
                log.info("fail");

            }
        }
        log.info("t-{}", jsonNode);
    }

    public JsonNode idCardValidate(String idCardNo, String name) {

        String url = UriComponentsBuilder.fromUriString(ID_CARD_HOST)
                .path(ID_CARD_PATH)
                .build()
                .toString();
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("idCardNo", idCardNo)
                .put("name", name);

        Map<String, String> body = objectMapper.convertValue(bodyValue, new TypeReference<TreeMap<String, String>>() {
        });
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.setAll(body);
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + ID_CARD_APPCODE))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(map))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .onErrorResume(throwable -> {
                        log.error("api:{}, client:{} ", url, throwable.getMessage());
                        if (throwable instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) throwable;
                            if (ex.getStatusCode().is4xxClientError()) {
                                JsonNode resp = Optional.ofNullable(ex.getResponseBodyAs(JsonNode.class)).orElse(NullNode.getInstance());
                                if (resp.has("code")) {
                                    return Mono.just(resp);
                                } else {
                                    return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Client Error", ex.getStatusCode().value()));
                                }
                            } else if (ex.getStatusCode().is5xxServerError()) {
                                return Mono.error(new CustomThirdPartyApiException(ex, url, "ThirdParty Server Error", ex.getStatusCode().value()));
                            }
                        }
                        return Mono.error(new CustomThirdPartyApiException(throwable, url, "ThirdParty  Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
                    });
            result = voidMono.block();
            log.debug("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, ID_CARD_PATH, "请求失败", 500);
        }
        if (result.has("code")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, ID_CARD_PATH);
            throw new CustomThirdPartyApiException(ID_CARD_PATH, "请求失败:" + result, 500);
        }
    }
}
