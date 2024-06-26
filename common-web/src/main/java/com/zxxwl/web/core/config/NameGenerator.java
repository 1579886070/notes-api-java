package com.zxxwl.web.core.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class NameGenerator extends AnnotationBeanNameGenerator {

    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry){
        return definition.getBeanClassName();
    }
}
