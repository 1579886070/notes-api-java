package com.zxxwl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis 统一配置
 * 序列化
 * 注意: 注入时不推荐使用@Autowired 进行注入,若非要使用结合@Qualifier 指定名称;建议使用
 *
 * @author qingyu 2023.02.13
 * @link com.zxxwl.user.service.config.JsonConfig#customJackson2ObjectMapperBuilder()
 */
@RequiredArgsConstructor
@Configuration
public class RedisConfig {
    private final ObjectMapper objectMapper;


    /**
     * 序列化配置
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 创建一个json的序列化方式
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(jackson2JsonRedisSerializer());
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

//        redisTemplate.setDefaultSerializer(serializer);
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 key 用 string 序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置 hash 的键
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 value 用 jackson 进行处理
        redisTemplate.setValueSerializer(serializer);
        // 设置 hash 的 value 序列化
//        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 采用相同的 序列化
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        // 创建一个json的序列化方式
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(jackson2JsonRedisSerializer());
        Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, String.class);


//        redisTemplate.setDefaultSerializer(serializer);
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 key 用 string 序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置 hash 的键
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 value 用 jackson 进行处理
        redisTemplate.setValueSerializer(serializer);
        // 设置 hash 的 value 序列化
//        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 原始 StringRedisTemplate string存入redis 无引号
     * {@code @Resource("baseStringRedisTemplate")private StringRedisTemplate baseStringRedisTemplate;}
     * </p>
     * {@code @Autowired @Qualifier("baseStringRedisTemplate")private StringRedisTemplate baseStringRedisTemplate;}
     * </p>
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @return StringRedisTemplate
     */
    @Bean("baseStringRedisTemplate")
    public StringRedisTemplate baseStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    /*@Deprecated
    private ObjectMapper jackson2JsonRedisSerializer() {
        return objectMapper;
        *//*return new Jackson2ObjectMapperBuilder()
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern)))
                .simpleDateFormat(pattern)
                .timeZone(timeZone)
                .indentOutput(false) // 缩进
                .modulesToInstall(new ParameterNamesModule())
                .modulesToInstall(new Jdk8Module())
                .modulesToInstall(new JavaTimeModule())
                .featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                // .featuresToDisable(MapperFeature.USE_ANNOTATIONS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//                .serializationInclusion(JsonInclude.Include.NON_NULL) // null 不序列化
                .build();*//*
    }*/
}