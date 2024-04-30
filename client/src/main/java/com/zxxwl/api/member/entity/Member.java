package com.zxxwl.api.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zxxwl.web.core.mvc.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 用户信息表
*
* @author zhouxin
* @since 2024-04-28 17:44:17
*/
@Getter
@Setter
@Accessors(chain = true)
@TableName("member")
public class Member extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 用户信息表主键
    */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
    * 账号
    */
    private String account;

    /**
    * 密码
    */
    private String password;

    /**
    * 昵称
    */
    private String nickname;

    /**
    * 手机号码
    */
    private String phone;

    /**
    * 头像路径
    */
    private String faceUrl;

    /**
    * 样式背景图
    */
    private String styleBgUrl;

    /**
    * 性别(1-男，2-女，0-未知)
    */
    private Boolean sex;

    /**
    * 出生年月日
    */
    private Long birth;

    /**
    * 真实姓名
    */
    private String realName;

    /**
    * 是否禁用(0-未禁用，1-禁用)
    */
    private Boolean forbidStatus;

    /**
    * 注册时间
    */
    private Long registerTime;

    /**
    * 更新时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
