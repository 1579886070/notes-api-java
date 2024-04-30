package com.zxxwl.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期格式化-全局统一配置
 * 结合:spring.mvc.format.dateTime
 * 暂不支持 接口形参 传时间
 * 日期格式化全局配置: 默认格式 yyyy-MM-dd HH:mm:ss
 * 范围:LocalDateTime 包含 LocalDateTime,LocalDate,LocalTime;一般日期格式化:java.util.Date,java.util.Calendar
 * 特殊格式请 使用 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 * 其他:请参考 @JsonComponent 或 implements WebMvcConfigurer
 *
 * @author qingyu   date: 2022.12.29
 */
@Slf4j
@Configuration

public class SysDateFormatConfig {

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;
    @Value("${spring.jackson.time-zone:GMT+8}")
    private String timeZone;

    /**
     * 时间-序列化-java8 time
     *
     * @return R
     */
    // @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 时间-反列化-java8 time
     *
     * @return R
     */
    // @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 日期格式化 序列化反序列化
     * LocalDateTime 包含 LocalDateTime,LocalDate,LocalTime
     * 一般日期格式化
     *
     * @return org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        // 默认LocalDateTime格式化的格式 yyyy-MM-dd HH:mm:ss
        log.debug("pattern :{},timeZone :{}", pattern, timeZone);
        return b -> b
                .serializerByType(LocalDateTime.class, localDateTimeSerializer())
                .deserializerByType(LocalDateTime.class, localDateTimeDeserializer())
                //.serializerByType(BigDecimal.class, new BigDecimalSerializer())
                //.deserializerByType(BigDecimal.class, new BigDecimalDeserializer())
                .simpleDateFormat(pattern)
                .timeZone(timeZone)
                .indentOutput(true)
                .modulesToInstall(new ParameterNamesModule())
                .modulesToInstall(new Jdk8Module())
                .modulesToInstall(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                ;
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

    /*
     * @return R
     * @link https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.spring-mvc.customize-jackson-objectmapper
     */
//    @Bean(name = "customJackson2ObjectMapperBuilder")
    /*public Jackson2ObjectMapperBuilder customJackson2ObjectMapperBuilder() {
        // 默认LocalDateTime格式化的格式 yyyy-MM-dd HH:mm:ss
        return new Jackson2ObjectMapperBuilder()
                .serializerByType(LocalDateTime.class, localDateTimeSerializer())
                .deserializerByType(LocalDateTime.class, localDateTimeDeserializer())
                .simpleDateFormat(pattern)
                .timeZone(timeZone)
                .indentOutput(true)
                .modulesToInstall(new ParameterNamesModule())
                .modulesToInstall(new Jdk8Module())
                .modulesToInstall(new JavaTimeModule())
                .featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                ;

    }*/
}