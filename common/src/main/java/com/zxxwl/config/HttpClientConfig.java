package com.zxxwl.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 网络连接客户端 配置
 */
@Configuration
public class HttpClientConfig {
    //20,100,800
    //    @Value("${http_max_total}")
    private int maxTotal = 100;
    // 5,10,80
    //    @Value("${http_default_max_perRoute}")
    private int defaultMaxPerRoute = 10;

    //    @Value("${http_validate_after_inactivity}")
    private int validateAfterInactivity = 1000;

    //    @Value("${http_connection_request_timeout}")
    private int connectionRequestTimeout = 5000;

    //    @Value("${http_connection_timeout}")
    private int connectTimeout = 10000;

    //    @Value("${http_socket_timeout}")
    private int socketTimeout = 20000;

    //    @Value("${waitTime}")
    private int waitTime = 30000;

    //    @Value("${idleConTime}")
    private int idleConTime = 3;

    //    @Value("${retryCount}")
    private int retryCount = 3;

    /**
     * 请求配置
     *
     * @return requestConfig
     */
//    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))     // 从连接池中取连接的超时时间
                .setResponseTimeout(Timeout.ofSeconds(5))
                .build();
    }

    /**
     * 连接配置
     *
     * @return connectionConfig
     */
//    @Bean
    public ConnectionConfig connectionConfig() {
        return ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(connectTimeout)) // 连接超时时间
                .setSocketTimeout(Timeout.ofSeconds(socketTimeout))
                .setValidateAfterInactivity(TimeValue.ofSeconds(validateAfterInactivity))
                .build();
    }

    //    @Bean
    public PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolmanager = new PoolingHttpClientConnectionManager();
        poolmanager.setMaxTotal(maxTotal);
        poolmanager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return poolmanager;
    }

    //    @Bean
    public PoolingAsyncClientConnectionManager asyncClientConnectionManager() {
        PoolingAsyncClientConnectionManager poolmanager = new PoolingAsyncClientConnectionManager();
        poolmanager.setMaxTotal(maxTotal);
        poolmanager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        poolmanager.setDefaultConnectionConfig(connectionConfig());
        return poolmanager;
    }

    /*@Bean
    public CloseableHttpClient createHttpClient(PoolingHttpClientConnectionManager poolManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(poolManager);
        httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (iterator.hasNext()) {
                    HeaderElement headerElement = iterator.nextElement();
                    String param = headerElement.getName();
                    String value = headerElement.getValue();
                    if (null != value && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return * 1000;
            }
        });
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, false));
        return httpClientBuilder.build();
    }*/


    //    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolManager) {
        return HttpClientBuilder.create().setConnectionManager(poolManager).build();
    }

    //    @Bean
    public CloseableHttpAsyncClient httpAsyncClient() {
        return HttpAsyncClientBuilder.create()
                .setConnectionManager(asyncClientConnectionManager())
                .setDefaultRequestConfig(requestConfig())
                .build();
    }

    /**
     * HttpClient
     *
     * @return httpClient
     */
//    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig())
                .build();
    }

    /**
     * WebClient
     *
     * @return webClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
//                .clientConnector(new HttpComponentsClientHttpConnector(httpAsyncClient()))
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }


    /*@Bean
    public IdleConnectionEvictor createIdleConnectionEvictor(PoolingHttpClientConnectionManager poolManager) {
        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(poolManager, waitTime, idleConTime);
        return idleConnectionEvictor;
    }*/
/**
 * 试错
 */
    /*public void retryHandler() {
        HttpRequestRetryStrategy retryHandler = new HttpRequestRetryStrategy() {
            int maxRetries=3;
            @Override
            public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
                if (execCount >= maxRetries) {
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                if (exception instanceof SSLException) {
                    return false;
                }
                *//*if (exception instanceof ConnectTimeoutException) {
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }*//*
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                if (exception instanceof HttpHostConnectException) {
                    return true;
                }
                if (exception instanceof UnknownHostException) {
                    return true;
                }
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
                if (execCount >= maxRetries) {
                    return false;
                }
                int code = response.getCode();
                if (code == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    return true;
                } else if (code == HttpStatus.SC_BAD_GATEWAY) {
                    return true;
                }
                return false;
            }

            @Override
            public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
                if (execCount >= maxRetries) {
                    return null;
                }
                int code = response.getCode();
                if (code == HttpStatus.SC_SERVICE_UNAVAILABLE) {
//                    return true;
                } else if (code == HttpStatus.SC_BAD_GATEWAY) {
//                    return true;
                }
                return null;
            }
        };
    }*/

}


