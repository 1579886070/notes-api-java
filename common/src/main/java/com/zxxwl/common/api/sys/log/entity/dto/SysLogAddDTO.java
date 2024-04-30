package com.zxxwl.common.api.sys.log.entity.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class SysLogAddDTO {
    /**
     * 主键
     */
    private String id;
    /**
     * 主要相关人
     */
    private String memberId;
    /**
     * 内容
     */
    private String content;
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private String type;
    /**
     * 来源
     */
    private String source;
    /**
     * ip
     */
    private String ip;
}
