package com.zxxwl.common.api.sys.thirdaccount.entity.dto;

import lombok.Data;

@Data
public class SysThirdAccountDto {
    /**
     * 主键
     */
    private String id;
    /**
     * 数据id|配置id
     */
    private String dataId;
    /**
     * 配置数据内容 默认json
     */
    private String content;
    /**
     * 配置数据内容类型
     * Text,Json,Xml,Yaml,Html,Properties
     */
    private String type;
}
