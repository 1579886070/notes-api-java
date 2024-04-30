package com.zxxwl.map;

import com.zxxwl.config.JsonConfig;
import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
public class MapAPITest {
    public static final String MAP_HOST = "https://qryloct.market.alicloudapi.com";
    public static final String MAP_PATH = "/lundear/qryaddr";
    public static final String MAP_PATH_V2 = "/lundear/qryloct";
    public static final String MAP_METHOD = "GET";
    public static final String MAP_APPCODE = "2afea7f3a94f4456add323c7755315c2";
    public ObjectMapper objectMapper = JsonConfig.getInstance();

    WebClient webClient = WebClient.builder().build();

    /**
     * {"status":0,"message":"queryok","result":{"title":"临平","location":{"lng":120.2817,"lat":30.43758},"ad_info":{"adcode":"330110"},"address_components":{"province":"浙江省","city":"杭州市","district":"余杭区","street":"","street_number":""},"similarity":0.99,"deviation":1000,"reliability":7,"level":3}}
     */
    @Test
    public void t() {

        JsonNode jsonNode = idCardValidate("杭州市临平区人民政府(龙王塘路东)");
//    int result = jsonNode.path("data").path("result").asInt();
        if (jsonNode.path("status").asInt() == 0) {

            String lng = jsonNode.path("result").path("location").path("lng").asText();
            int location = jsonNode.path("result").path("location").intValue();
            String lat = jsonNode.path("result").path("location").path("lat").asText();


            String province = jsonNode.path("result").path("address_components").path("province").asText();
            String city = jsonNode.path("result").path("address_components").path("city").asText();
            String district = jsonNode.path("result").path("address_components").path("district").asText();


            log.info("成功,{},经度,{},维度,{}", "成功", province, city);
            log.info("成功,{},-------,{}", "成功", jsonNode.toString());
            System.out.println();
        } else {
            log.info("result,{}", jsonNode.path("massage"));
        }


    }

    @Test
    public void t2() {

        JsonNode jsonNode = getAddress("120.30503", "30.39663", 0);
//    int result = jsonNode.path("data").path("result").asInt();
        if (jsonNode.path("status").asInt() == 0) {

            String address = jsonNode.path("result").path("address").asText();
            int location = jsonNode.path("result").path("location").intValue();
            String lat = jsonNode.path("result").path("location").path("lat").asText();

            String province = jsonNode.path("result").path("address_components").path("province").asText();
            String city = jsonNode.path("result").path("address_components").path("city").asText();
            String district = jsonNode.path("result").path("address_components").path("district").asText();


            log.info("成功,{},经度,{},维度,{},{}", "成功", province, city, address);
            log.info("成功,{},-------,{}", "成功", jsonNode.toString());
            log.info("成功,{},-------,{}", "成功", lat);

            System.out.println();
        } else {
            log.info("result,{}", jsonNode.path("massage"));
        }

    }


    public JsonNode idCardValidate(String address) {

        String url = UriComponentsBuilder.fromUriString(MAP_HOST)
                .path(MAP_PATH)
                .queryParam("address", address)
                .build()
                .toString();
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MAP_APPCODE))
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
            throw new CustomThirdPartyApiException(e, MAP_PATH, "请求失败", 500);
        }
        if (result.path("status").asInt(-1) == 0) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, MAP_PATH);
            throw new CustomThirdPartyApiException(MAP_PATH, "请求失败:" + result, 500);
        }
    }/* public JsonNode idCardValidate(String address) {

        String url = UriComponentsBuilder.fromUriString(MAP_HOST)
                .path(MAP_PATH)
                .queryParam("address", address)
                .build()
                .toString();
        JsonNode result = null;
        try {
            Mono<JsonNode> voidMono = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MAP_APPCODE))
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
            throw new CustomThirdPartyApiException(e, MAP_PATH, "请求失败", 500);
        }
        if (result.has("status")) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, MAP_PATH);
            throw new CustomThirdPartyApiException(MAP_PATH, "请求失败:" + result, 500);
        }
    }*/

    public JsonNode getAddress(String longitude, String latitude, int poi) {

        String url = UriComponentsBuilder.fromUriString(MAP_HOST)
                .path(MAP_PATH_V2)
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
                    .headers(httpHeaders -> httpHeaders.add("Authorization", "APPCODE " + MAP_APPCODE))
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
            throw new CustomThirdPartyApiException(e, MAP_PATH, "请求失败", 500);
        }
        if (result.path("status").asInt(-1) == 0) {
            return result;
        } else {
            log.error("fail result:{},api:{}", result, MAP_PATH);
            throw new CustomThirdPartyApiException(MAP_PATH, "请求失败:" + result, 500);
        }
    }

}
