package com.zxxwl.api.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zxxwl.web.core.mvc.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 文章表
*
* @author zhouxin
* @since 2024-04-28 17:44:43
*/
@Getter
@Setter
@Accessors(chain = true)
@TableName("article")
public class Article extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 文章id
    */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
    * 用户id
    */
    private String memberId;

    /**
    * 标题
    */
    private String title;

    /**
    * 封面
    */
    private String cover;

    /**
    * 内容
    */
    private String content;

    /**
    * 分享数
    */
    private Integer share;

    /**
    * 点赞数
    */
    private Integer approve;

    /**
    * 评论数
    */
    private Integer comment;

    /**
    * 下载数
    */
    private Integer download;

    /**
    * 观看数
    */
    private Integer pv;

    /**
    * 说明
    */
    private String brief;

    /**
    * 状态（0-禁用，1-可用）
    */
    @TableField(fill = FieldFill.INSERT)
    private Boolean status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long pushTime;

    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
