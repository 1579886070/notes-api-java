package com.zxxwl.config;

import com.zxxwl.common.utils.YmlPropertiesUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;

/**
 * json 配置
 * jackson
 *
 * @author qingyu
 */
@Slf4j
@Configuration
public class JsonConfig {
    //    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private static final String pattern;
    //    @Value("${spring.jackson.time-zone:GMT+8}")
    private static final String timeZone;
    private static ObjectMapper objectMapper;

    static {
        String pt = null, tz = null;
        try {
            YmlPropertiesUtil ymlPropertiesUtil = new YmlPropertiesUtil();
            Properties properties = ymlPropertiesUtil.getProperties("application.yml");
            if (Objects.nonNull(properties)) {
                pt = properties.getProperty("spring.jackson.date-format");
                tz = properties.getProperty("spring.jackson.time-zone");
            }
        } finally {
            pattern = pt == null ? "yyyy-MM-dd HH:mm:ss" : pt;
            timeZone = tz == null ? "GMT+8" : tz;
        }

    }

    @Bean(name = "objectMapper")
    public ObjectMapper customJackson2ObjectMapperBuilder() {
        return getInstance();
    }

    /*private static class BigDecimalSerializer extends com.fasterxml.jackson.databind.JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, com.fasterxml.jackson.core.JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider serializers) throws java.io.IOException {
            gen.writeString(value.setScale(2, RoundingMode.HALF_UP).toString());
        }
    }

    private static class BigDecimalDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String value = jsonParser.getValueAsString();
            if (value != null) {
                // 将字符串转换为 BigDecimal，并保留两位小数
                return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
            }
            return null;
        }
    }*/
    public static ObjectMapper getInstance() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = Jackson2ObjectMapperBuilder.json()
                    .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)))
                    .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern)))
                    // .serializerByType(BigDecimal.class, new BigDecimalSerializer())
                    // .deserializerByType(BigDecimal.class, new BigDecimalDeserializer())
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
//                .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
//                .featuresToEnable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                    .featuresToDisable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                    .build();
            // 此项 解决 error java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
                /*.build().activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY);*/
        }
        // 默认LocalDateTime格式化的格式 yyyy-MM-dd HH:mm:ss
        return objectMapper;
    }

}
