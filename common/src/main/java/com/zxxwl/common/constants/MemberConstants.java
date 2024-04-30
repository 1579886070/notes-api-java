package com.zxxwl.common.constants;

/**
 * 用户常量
 *
 * @author zhouxin
 * @since 2020/11/3 10:40
 */
public class MemberConstants {

    /**
     * 账号可用
     */
    public static final int NO_DISABLE = 0;
    /**
     * 账号禁用
     */
    public static final int DISABLE = 1;

    /**
     * 会员邀请码默认初始长度
     */
    public static final int USER_INVITATION_CODE_LENGTH = 7;

    /**
     * 店铺邀请码默认初始长度
     */
    public static final int SHOP_INVITATION_CODE_LENGTH = 7;

    /**
     * 实名认证来源 0-未认证
     */
    public static final int DEFAULT_AUTH_SOURCE = 0;

    /**
     * 用户性别 0-未知
     */
    public static final int DEFAULT_SEX = 0;

    /**
     * 默认用户头像地址
     */
    public static final String DEFAULT_MEMBER_PHOTO = "https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/resources/zxxwl/image/member_default_photo.png";

    /**
     * 默认导航图标（用作地图）
     */
    public static final String DEFAULT_MEMBER_NAVIGATION_PHOTO = "https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/resources/zxxwl/image/member_default_naviation_photo.png";

    /**
     * 默认用户背景图片地址
     */
    public static final String DEFAULT_MEMBER_BACKGROUND = "https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/resources/image/20221025/20221025145507-e47ce02c63b046bc9def4ffa6e90b131.png";

/*    public static final String DEFAULT_MEMBER_BACKGROUND = "https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/resources/zxxwl/image/member_default_background.png";*/
    /**
     * 是否禁用 0-未禁用
     */
    public static final int DEFAULT_FORBID_STATUS = 0;

    /**
     * 会员角色 0-普通会员
     */
    public static final int DEFAULT_MEMBER_ROLE = 0;

    /**
     * 信息不存在
     */
    public static final int NO_EXIST_INFO = 0;
    /**
     * 信息存在
     */
    public static final int EXIST_INFO = 1;

    /**
     * 邀请码校验必须
     */
    public static final boolean INVITE_CODE_CHECK_MUST = true;

    /**
     * 注册时是否弹出邀请码框
     */
    public static final boolean POP_UP_INVITE_CODE = true;

    /**
     * 登录类型 短信登录
     */
    public static final String LOGIN_TYPE_SMS = "短信登录";
    /**
     * 登录类型 号码认证
     */
    public static final String LOGIN_TYPE_PHONE_AUTH = "一键登录";
    /**
     * 登录类型 微信登录
     */
    public static final String LOGIN_TYPE_PHONE_WX = "微信登录";
    /**
     * 登录类型 授权登录
     */
    public static final String LOGIN_TYPE_PHONE_TOKEN = "授权登录";

    /**
     * 支付类型/支付方式：支付宝
     */
    public static final int ALIPAY_ACCOUNT_TYPE = 0;
    /**
     * 支付类型/支付方式：微信
     */
    public static final int WEI_XIN_ACCOUNT_TYPE = 1;
    /**
     * 支付类型/支付方式：H5（JSAPI）
     */
    public static final int H5_JSAPI_ACCOUNT_TYPE = 2;
    /**
     * 支付类型/支付方式：站外H5（H5）
     */
    public static final int H5_ACCOUNT_TYPE = 3;
    /**
     * 支付类型/支付方式：支付宝（手机网站支付-H5）
     */
    public static final int ALIPAY_H5_ACCOUNT_TYPE = 4;
    /**
     * 支付类型/支付方式：小程序
     */
    public static final int H5_APPLE_JSAPI_ACCOUNT_TYPE = 5;

    /**
     * 角色类型-大V
     */
    public static final int SUPER_MEMBER_ROLE = 1;
    /**
     * 角色类型-宠它（普通会员）
     */
    public static final int MEMBER_ROLE = 2;
    /**
     * 角色类型-它美（美容师）
     */
    public static final int BEAUTICIAN_ROLE = 3;
    /**
     * 角色类型-宠店（店铺）
     */
    public static final int SHOP_ROLE = 4;
    /**
     * 角色类型-宠物证件
     */
    public static final int PET_CARD_ROLE = 5;
    /**
     * 消费类型-宠物保险
     */
    public static final int INSURANCE_ROLE = 6;

    /**
     * 用户实名认证状态-未实名认证
     */
    public static final int AUTH_STATUS_NO = 0;
    /**
     * 用户实名认证状态-已实名认证
     */
    public static final int AUTH_STATUS_OK = 1;

    /**
     * ”只允许我关注的评论我“，评论状态开关（0-关闭，1-开启）
     */
    public static final int COMMENT_STATUS_NO = 0;
    public static final int COMMENT_STATUS_YES = 1;

    /**
     * 注册时过滤的手机号
     */
    public static final String PASS_PHONE = "19558156566";

    /**
     * 是否绑定微信(0-否，1-是)
     */
    public static final int IS_BIND_WX_NO = 0;
    public static final int IS_BIND_WX_OK = 1;

    /**
     * 测试账号，登录跳过的手机号/验证码
     */
    public static final String TEST_PHONE = "18888888888";
    public static final String TEST_PHONE_TWO = "18888888887";
    public static final String TEST_PHONE_THREE = "18888888886";
    public static final String TEST_SMS = "000000";

    /**
     * 提现费率，如果等于0则免费
     */
    public static double EXTRACT_RATE = 0.01;
}
