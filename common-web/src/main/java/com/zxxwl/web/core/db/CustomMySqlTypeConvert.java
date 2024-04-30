package com.zxxwl.web.core.db;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.LONG;

public class CustomMySqlTypeConvert extends MySqlTypeConvert {

    @Override
    public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
        if (fieldType.contains("bigint")) {
            return LONG;
            // return BIG_INTEGER;
        } else if (fieldType.toLowerCase().contains("byte")) {
            return INTEGER;
        }
        return super.processTypeConvert(config, fieldType);
    }
}