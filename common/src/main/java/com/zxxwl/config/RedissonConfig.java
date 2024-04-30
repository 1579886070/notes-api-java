package com.zxxwl.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson工具类
 *
 * @author zhouxin 2020/10/31 17:10
 * @author qingyu 2023
 * @apiNote 这里注意，在其他项目引用时，需要在启动上指定扫描这个类所在的包，并且使用@Autowired或其他方式进行注入后才能用
 */
@Configuration
public class RedissonConfig {
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${spring.data.redis.host}")
    private String addresses;
    @Value("${spring.data.redis.port}")
    private String port;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.database:2}")
    private int database;
    private int connectionMinimumIdleSize = 10;
    private int idleConnectionTimeout = 10000;
    private int pingTimeout = 1000;
    private int connectTimeout = 10000;
    private int timeout = 3000;
    private int retryAttempts = 3;
    private int retryInterval = 1500;
    private int reconnectionTimeout = 3000;
    private int failedAttempts = 3;
    private int subscriptionsPerConnection = 5;
    private String clientName = null;
    private int subscriptionConnectionMinimumIdleSize = 1;
    private int subscriptionConnectionPoolSize = 50;
    private int connectionPoolSize = 64;
    private boolean dnsMonitoring = false;
    private int dnsMonitoringInterval = 5000;
    private int thread;//当前处理核数量*2
    private static NioEventLoopGroup nioEventLoopGroup;

    private static NioEventLoopGroup getNioEventLoopGroup() {
        if (nioEventLoopGroup == null) {
            nioEventLoopGroup = new NioEventLoopGroup();
        }
        return nioEventLoopGroup;
    }


    /**
     * 单例 bean 不需要关闭
     *
     * @return redissonClient
     * @throws Exception
     */
    @Bean
    public RedissonClient redissonClient() throws Exception {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + addresses + ":" + port)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setConnectionPoolSize(connectionPoolSize)
                .setDatabase(database)
                //.setDnsMonitoring(dnsMonitoring)
                .setDnsMonitoringInterval(dnsMonitoringInterval)
                .setSubscriptionConnectionMinimumIdleSize(subscriptionConnectionMinimumIdleSize)
                .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)
                .setSubscriptionsPerConnection(subscriptionsPerConnection)
                .setClientName(clientName)
//                .setFailedAttempts(failedAttempts)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval)
//                .setReconnectionTimeout(reconnectionTimeout)
                .setTimeout(timeout)
                .setConnectTimeout(connectTimeout)
                .setIdleConnectionTimeout(idleConnectionTimeout)
//                .setPingTimeout(pingTimeout)
                .setPassword(password);
//        Codec codec = (Codec) ClassUtils.forName("org.redisson.codec.JsonJacksonCodec", ClassUtils.getDefaultClassLoader()).newInstance();
        // JsonJacksonCodec
        // config.setCodec(new JsonJacksonCodec(objectMapper));
        // Jackson JSON codec which doesn't store type id (@class field) during encoding and doesn't require it for decoding
        config.setCodec(new TypedJsonJacksonCodec(
                new TypeReference<Object>() {
                },
                new TypeReference<Object>() {
                },
                new TypeReference<Object>() {
                },
                objectMapper));
//        config.setCodec(new JsonJacksonCodecSmile(objectMapper));
        config.setThreads(thread);
        config.setEventLoopGroup(getNioEventLoopGroup());
        //config.setUseLinuxNativeEpoll(false);
        return Redisson.create(config);
    }

    @Bean("jsonRedissonClient")
    public RedissonClient jsonRedissonClient() throws Exception {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + addresses + ":" + port)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setConnectionPoolSize(connectionPoolSize)
                .setDatabase(database)
                //.setDnsMonitoring(dnsMonitoring)
                .setDnsMonitoringInterval(dnsMonitoringInterval)
                .setSubscriptionConnectionMinimumIdleSize(subscriptionConnectionMinimumIdleSize)
                .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)
                .setSubscriptionsPerConnection(subscriptionsPerConnection)
                .setClientName(clientName)
//                .setFailedAttempts(failedAttempts)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval)
//                .setReconnectionTimeout(reconnectionTimeout)
                .setTimeout(timeout)
                .setConnectTimeout(connectTimeout)
                .setIdleConnectionTimeout(idleConnectionTimeout)
//                .setPingTimeout(pingTimeout)
                .setPassword(password);
//        Codec codec = (Codec) ClassUtils.forName("org.redisson.codec.JsonJacksonCodec", ClassUtils.getDefaultClassLoader()).newInstance();
        // JsonJacksonCodec
        // config.setCodec(new JsonJacksonCodec(objectMapper));
        // Jackson JSON codec which doesn't store type id (@class field) during encoding and doesn't require it for decoding
        config.setCodec(new TypedJsonJacksonCodec(
                new TypeReference<JsonNode>() {
                },
                new TypeReference<JsonNode>() {
                },
                new TypeReference<JsonNode>() {
                },
                objectMapper));
//        config.setCodec(new JsonJacksonCodecSmile(objectMapper));
        config.setThreads(thread);
        config.setEventLoopGroup(getNioEventLoopGroup());
        //config.setUseLinuxNativeEpoll(false);
        return Redisson.create(config);
    }

    /*@Bean
    public RedissonGeoService<ObjectNode> redissonGeoService() {
        return new RedissonGeoServiceImpl<>(new TypedJsonJacksonCodec(new TypeReference<ObjectNode>() {
        }, objectMapper));
    }

    @Bean
    public RedissonGeoService<String> stringRedissonGeoService() {
        return new RedissonGeoServiceImpl<>(new TypedJsonJacksonCodec(new TypeReference<String>() {
        }, objectMapper));
    }*/

}