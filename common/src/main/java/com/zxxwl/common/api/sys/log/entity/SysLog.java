package com.zxxwl.common.api.sys.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * id
 * member_id
 * type
 * source
 * title
 * content
 * status
 * create_time
 * update_time
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("system_log")
public class SysLog {
    /**
     * 主键
     */
    private String id;
    /**
     * 主要相关人
     */
    private String memberId;
    /**
     * 类型
     */
    private String type;
    /**
     * 来源
     */
    private String source;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * ip
     */
    private String ip;
    /**
     * 状态
     */
    private Boolean df;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 修改时间
     */
    private Long updateTime;
}
