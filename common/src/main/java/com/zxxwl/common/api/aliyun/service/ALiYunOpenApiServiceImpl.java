package com.zxxwl.common.api.aliyun.service;


import com.zxxwl.common.api.sys.thirdaccount.service.SysThirdAccountService;
import com.zxxwl.common.constants.ALYConstants;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

import static com.zxxwl.common.constants.SysThirdAccountContents.ALIYUN_MARKET_APP;


@Slf4j
@RequiredArgsConstructor
@Service
public class ALiYunOpenApiServiceImpl implements ALiYunOpenApiService {
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final SysThirdAccountService sysThirdAccountService;
    public static String MARKET_APPCODE;

    @PostConstruct
    private void init() {
        JsonNode jsonNode = sysThirdAccountService.queryJsonContentByDataId(ALIYUN_MARKET_APP);
        MARKET_APPCODE = jsonNode.path("appcode").asText();
    }


    @Deprecated
    @Override
    public JsonNode idCardValidate(String idCardNo, String name) {

        String url = UriComponentsBuilder.fromUriString(ALYConstants.MARKET_ID_CARD_HOST).path(ALYConstants.MARKET_ID_CARD_PATH).build().toString();
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("idCardNo", idCardNo).put("name", name);

        Map<String, String> body = objectMapper.convertValue(bodyValue, new TypeReference<TreeMap<String, String>>() {
        });
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.setAll(body);
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MARKET_APPCODE))
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
            log.info("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, ALYConstants.MARKET_ID_CARD_HOST, "请求失败", 500);
        }
        if (result.has("code")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, ALYConstants.MARKET_ID_CARD_PATH);
            throw new CustomThirdPartyApiException(ALYConstants.MARKET_ID_CARD_PATH, "请求失败:" + result, 500);
        }
    }

    @Override
    public JsonNode idCardValidateV2(String idCardNo, String name) {

        String url = UriComponentsBuilder.fromUriString(ALYConstants.MARKET_ID_CARD_HOST)
                .path(ALYConstants.MARKET_ID_CARD_PATH)
                .build()
                .toString();
        ObjectNode bodyValue = objectMapper.createObjectNode();
        bodyValue.put("idcard", idCardNo)
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
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MARKET_APPCODE))
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
            throw new CustomThirdPartyApiException(e, ALYConstants.MARKET_ID_CARD_PATH, "请求失败", 500);
        }
        if (result.has("code")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, ALYConstants.MARKET_ID_CARD_PATH);
            throw new CustomThirdPartyApiException(ALYConstants.MARKET_ID_CARD_PATH, "请求失败:" + result, 500);
        }
    }

    @Override
    public JsonNode getCoordinate(String address) {
        String url = UriComponentsBuilder.fromUriString(ALYConstants.MARKET_MAP_GEOCODE_HOST)
                .path(ALYConstants.MARKET_MAP_GEOCODE_ADDRESS)
                .queryParam("address", address)
                .build()
                .toString();
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MARKET_APPCODE))
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
            log.info("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, ALYConstants.MARKET_MAP_GEOCODE_ADDRESS, "请求失败", 500);
        }
        if (result.path("status").asInt(-1) == 0) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, ALYConstants.MARKET_MAP_GEOCODE_ADDRESS);
            throw new CustomThirdPartyApiException(ALYConstants.MARKET_MAP_GEOCODE_ADDRESS, "请求失败:" + result, 500);
        }
    }

    @Override
    public JsonNode getAddress(String longitude, String latitude, int poi) {
        String url = UriComponentsBuilder.fromUriString(ALYConstants.MARKET_MAP_GEOCODE_HOST)
                .path(ALYConstants.MARKET_MAP_GEOCODE_LNG_LAT_ADDRESS)
                .queryParam("lng", longitude)
                .queryParam("lat", latitude)
                .queryParam("poi", poi)
                .build()
                .toString();
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MARKET_APPCODE))
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
            log.info("rest:{}", result);
        } catch (Exception e) {
            throw new CustomThirdPartyApiException(e, ALYConstants.MARKET_MAP_GEOCODE_ADDRESS, "请求失败", 500);
        }
        if (result.path("status").asInt(-1) == 0) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, ALYConstants.MARKET_MAP_GEOCODE_ADDRESS);
            throw new CustomThirdPartyApiException(ALYConstants.MARKET_MAP_GEOCODE_ADDRESS, "请求失败:" + result, 500);
        }
    }
}
