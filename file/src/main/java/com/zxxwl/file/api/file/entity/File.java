package com.zxxwl.file.api.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 文件表
 *
 * @author qingyu
 * @since 2023-09-05 11:31:41
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("file")
public class File extends Model<File> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件表主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    private String memberId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 新文件名
     */
    private String newName;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件大小
     */
    private Long size;

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
