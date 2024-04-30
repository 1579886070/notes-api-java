package com.zxxwl.code;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

public class CustomMySqlTypeConvert extends MySqlTypeConvert {

    @Override
    public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
        if (fieldType.contains("bigint")) {
            return DbColumnType.LONG;
            // return BIG_INTEGER;
        } else if (fieldType.toLowerCase().contains("byte")) {
            return DbColumnType.INTEGER;
        }
        return super.processTypeConvert(config, fieldType);
    }
}