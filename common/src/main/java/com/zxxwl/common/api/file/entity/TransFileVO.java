package com.zxxwl.common.api.file.entity;

import lombok.Data;

/**
 * 上传文件 返回实体类
 */
@Data
public class TransFileVO {
    /**
     * 文件主键
     */
    private String id;
    /**
     * 文件 Url
     */
    private String url;
}
