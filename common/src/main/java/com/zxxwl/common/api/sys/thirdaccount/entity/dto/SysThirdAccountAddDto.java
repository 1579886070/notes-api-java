package com.zxxwl.common.api.sys.thirdaccount.entity.dto;

import lombok.Data;

@Data
public class SysThirdAccountAddDto {
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

    /**
     * 分类 Account | Token
     */
    private String category;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 描述简介
     */
    private String brief;
    /**
     * 过期时间
     */
    private Long expiresTime;
}
