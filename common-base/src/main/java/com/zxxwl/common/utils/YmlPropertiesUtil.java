package com.zxxwl.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * @author qingyu
 * @since 2021-07-16 8:55
 */
@Slf4j
public final class YmlPropertiesUtil {

    public Properties getProperties(String path) {
        Properties properties;
        try {
            // 可以加载多个yml文件
            YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
            yamlMapFactoryBean.setResources(new ClassPathResource(path));
            properties = yamlMapFactoryBean.getObject();
        } catch (Exception e) {
            log.info("YmlPropertiesUtil:{}", e.getMessage());
            properties = null;
        }
        //获取yml里的参数
        return properties;
    }

}