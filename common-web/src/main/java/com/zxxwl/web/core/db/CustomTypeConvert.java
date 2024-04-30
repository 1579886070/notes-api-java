package com.zxxwl.web.core.db;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;

@Deprecated
public class CustomTypeConvert implements ITypeConvert {
    @Override
    public DbColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        // 自定义类型转换逻辑
        if (fieldType.toLowerCase().contains("tinyint")) {
            return DbColumnType.INTEGER;
        }
        if (fieldType.toLowerCase().contains("byte")) {
            return DbColumnType.INTEGER;
        }
        // 返回默认的类型转换
        return null;
    }
}