package com.zxxwl.common.constants;

import java.util.List;

public class SysLoginConstants {
    public static final List<String> SUPPORT_ACCOUNT_STATUS = List.of("NotLogin", "Unregistered", "Disabled", "Unknow");

    /**
     * `NotLogIn`=未登录，`Unregistered`=未注册,`Disabled`=已禁用,`Unknow`=未知
     */
    public static final String ACCOUNT_STATUS_NOTLOGIN = "NotLogin";
    public static final String ACCOUNT_STATUS_UNREGISTERED = "Unregistered";
    public static final String ACCOUNT_STATUS_DISABLED = "Disabled";
    public static final String ACCOUNT_STATUS_UNKNOW = "Unknow";
    public static final String ACCOUNT_MSG_NOTLOGIN = "未登录或登录已失效，请重新登录！";
    public static final String ACCOUNT_MSG_UNREGISTERED = "未注册或未绑定";

}
