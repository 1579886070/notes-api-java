package com.zxxwl.test.common.json;

import com.zxxwl.config.JsonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
@Slf4j
public class JsonTest {
    @Test
    public void t(){
        ObjectMapper objectMapper = JsonConfig.getInstance();
        try {
            String s = objectMapper.writeValueAsString(Map.of("a", "1"));
            log.info("{}",s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
