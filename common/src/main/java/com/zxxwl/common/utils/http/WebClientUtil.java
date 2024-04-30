package com.zxxwl.common.utils.http;


import com.zxxwl.exception.CustomThirdPartyApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Optional;


/**
 * 目前仅服务于 Tim,wx
 * 第三方服务返回内容类型一般为JSON,即 {@code Content-Type:application/json; encoding=utf-8}
 * 例外情况则进一步处理
 *
 * @author qingyu
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class WebClientUtil {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * @param url url
     * @return json
     */
    public JsonNode get(String url) {
        Mono<JsonNode> voidMono = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
//                .header("Content-Type","application/json")
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
        return voidMono.block();
    }

    /**
     * json string to json object
     * Content-Type:text/plain
     * 响应体是json 字符串
     * 注意:该方法是为适配部分第三方平台接口不规范
     *
     * @param url url
     * @return result json
     */
    @SneakyThrows
    public JsonNode getJsonStrToJson(String url) {
        Mono<String> voidMono = webClient.get()
                .uri(url)
                .accept(MediaType.TEXT_PLAIN)
//                .header("Content-Type","application/json")
                .retrieve()
                .bodyToMono(String.class)
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
        String block = voidMono.block();
        return objectMapper.readValue(block, JsonNode.class);
    }

    /**
     * 返回json 对象
     *
     * @param url       url
     * @param bodyValue bodyValue
     * @return json
     */
    public JsonNode post(String url, @NotNull Object bodyValue) {
        Mono<JsonNode> voidMono = webClient.post()
                .uri(url)
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
        JsonNode rest = voidMono.block();
        log.debug("rest:{}", rest);
        return rest;
    }

    /**
     * 返回json 对象
     *
     * @param url       url
     * @param bodyValue bodyValue
     * @return json
     */
    @SneakyThrows
    public JsonNode postXmlResultJson(String url, @NotNull Object bodyValue) {
        Mono<String> voidMono = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_XML)
//                .bodyValue(BodyInserters.fromValue(bodyValue))
                .bodyValue(bodyValue)
                .retrieve()
                .bodyToMono(String.class)
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

        String restXml = voidMono.block();
        XmlMapper xmlMapper = new XmlMapper();
        ObjectNode rest = xmlMapper.readValue(restXml, ObjectNode.class);
        log.debug("rest:{}", rest);
        return rest;
    }

    /**
     * post 无 body 请求较少
     *
     * @param url url
     * @return JsonNode
     */
    public JsonNode post(String url) {
        Mono<JsonNode> voidMono = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
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
        JsonNode rest = voidMono.block();
        log.debug("rest:{}", rest);
        return rest;
    }

    /**
     * 单个 使用 Mono ,多个使用 Flux
     *
     * @param url       url
     * @param bodyValue bodyValue
     */
    @SneakyThrows
    public Resource postFileAsResource(String url, Object bodyValue) {
        Mono<Resource> resourceMono = webClient.post()
                .uri(url)
                .accept(MediaType.IMAGE_JPEG)
                .bodyValue(bodyValue)
                .exchangeToMono(resp -> {
                    if (resp.statusCode().equals(HttpStatus.OK)) {
                        Optional<MediaType> mediaType = resp.headers().contentType();
                        boolean present = mediaType.isPresent();
                        if (present) {
                            MediaType contentType = mediaType.get();
                            if (!MediaType.APPLICATION_JSON.getType().equals(contentType.getType())) {
                                log.debug("contentType:{}", contentType);
                                return resp.bodyToMono(Resource.class);
                            }
                        }
                    }
                    return Mono.error(new CustomThirdPartyApiException(url, "ThirdParty  Error:" + resp, HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }).onErrorResume(throwable -> {
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
        return resourceMono.block();

    }

    /**
     * 下载文件 返回 buffer
     *
     * @param url       url
     * @param bodyValue bodyValue
     * @return buffer
     */
    @SneakyThrows
    public ByteBuffer postFileAsBuffer(String url, Object bodyValue) {
        Mono<ByteBuffer> resourceMono = webClient.post()
                .uri(url)
                .accept(MediaType.IMAGE_JPEG)
                .bodyValue(bodyValue)
                .exchangeToMono(resp -> {
                    if (resp.statusCode().equals(HttpStatus.OK)) {
                        Optional<MediaType> mediaType = resp.headers().contentType();
                        boolean present = mediaType.isPresent();
                        if (present) {
                            MediaType contentType = mediaType.get();
                            if (!MediaType.APPLICATION_JSON.getType().equals(contentType.getType())) {
                                log.debug("contentType:{}", contentType);
                                return resp.bodyToMono(ByteBuffer.class);
                            }
                        }
                    }
                    Mono<JsonNode> jsonNodeMono = resp.bodyToMono(JsonNode.class);
                    log.error("ThirdParty  Error Resp:{}", jsonNodeMono);
                    return Mono.error(new CustomThirdPartyApiException(url, "ThirdParty  Error Resp:" + jsonNodeMono, HttpStatus.INTERNAL_SERVER_ERROR.value()));
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
        return resourceMono.block();

    }

    /**
     * 异步操作 单对象
     * {@code voidMono.subscribe((rest) -> {
     * errorCode.set(rest.get("ErrorCode").intValue());
     * log.debug("errorCode:{}", errorCode.get());
     * });}
     *
     * @param url       url
     * @param bodyValue bodyValue
     * @return mono
     */
    public Mono<JsonNode> postAsync(String url, Object bodyValue) {
        return webClient.post()
                .uri(url)
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
    }
}
