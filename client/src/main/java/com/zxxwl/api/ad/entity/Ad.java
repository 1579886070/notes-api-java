package com.zxxwl.api.ad.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zxxwl.web.core.mvc.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 广告
*
* @author zhouxin
* @since 2024-05-10 23:31:56
*/
@Getter
@Setter
@Accessors(chain = true)
@TableName("ad")
public class Ad extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 广告主键
    */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
    * 广告源内容 json|url
    */
    private String content;

    /**
    * 源类型:	[小程序weapp|公众号offiaccount|H5|图片image|视频video|待定~other]
    */
    private String originType;

    /**
    * 媒体载体类型:[image|video|json|page]
    */
    private String mediaType;

    /**
    * 状态（0-禁用，1-可用）
    */
    @TableField(fill = FieldFill.INSERT)
    private Boolean status;

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
