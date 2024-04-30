package com.zxxwl.common.constants;

import java.util.Collection;
import java.util.Set;

/**
 * 第三方账号 Open API
 *
 * @author qingyu 2023.09.08
 */
public class SysThirdAccountContents {
    /**
     * 小它服务-客户端-微信小程序-Account
     */
    public static final String ID_XT_FW_CLIENT_WX_MINI = "xt_fw_client_wx_mini";
    /**
     * 小它服务-商家端-微信小程序-Account
     */
    public static final String ID_XT_FW_MERCHANT_WX_MINI = "xt_fw_merchant_wx_mini";
    /**
     * 小它服务-客户端-微信小程序-accessToken
     */
    public static final String ID_TOKEN_XT_FW_CLIENT_WX_MINI = "token_xt_fw_client_wx_mini";
    /**
     * 阿里云市场 appcode
     */
    public static final String ALIYUN_MARKET_APP = "aliyun_market_app";
    /**
     * 企业微信 群机器人
     */
    public static final String WORKWX_TEAM_BOT = "work_wx_team_bot_dev";
    /**
     * 企业微信 群机器人 test
     */
    public static final String WORKWX_TEAM_BOT_DEV2 = "work_wx_team_bot_dev_2";
    /**
     * 支付宝|阿里支付
     */
    public static final String PAY_ALIPAY = "alipay";
    /**
     * 钱宝科技
     */
    public static final String PAY_GLOBEBILL = "globebill";
    /**
     * 受支持的
     */
    public static final Collection<String> SOURCE_IDS_SUPPORT = Set.of(ID_XT_FW_CLIENT_WX_MINI, ID_XT_FW_MERCHANT_WX_MINI,
            PAY_ALIPAY, PAY_GLOBEBILL, WORKWX_TEAM_BOT, WORKWX_TEAM_BOT_DEV2, ALIYUN_MARKET_APP);
    /**
     * 支持的类型
     */
    public static final Collection<String> TYPE_SUPPORT = Set.of("Text", "Json", "Xml", "Yaml", "Html", "Properties");
    /**
     * 支持的分类
     */
    public static final Collection<String> CATEGORY_SUPPORT = Set.of("Account", "Token");
}
