package com.zxxwl.common.constants;

import java.util.List;

public class PayConstants {
    // 支付方式类型：`WxMiniPay`=微信小程序，`Alipay`=支付宝，`Globebill`=钱宝科技-融合支付,`System`=后台系统设置）
    public static final String PAY_PLATFORM_GLOBEBILL = "Globebill";
    public static final String PAY_PLATFORM_WXMINIPAY = "WxMiniPay";
    public static final String PAY_PLATFORM_ALIPAY = "Alipay";
    public static final String PAY_PLATFORM_SYSTEM = "System";
    public static final List<String> SUPPORT_PAY_PLATFORM = List.of(PAY_PLATFORM_GLOBEBILL);
    /**
     * SUCCESS：支付成功
     * * REFUND：转入退款
     * * NOTPAY：未支付
     * * CLOSED：已关闭
     */
    public static final String PAY_STATUS_SUCCESS = "SUCCESS";
    public static final String PAY_STATUS_NOTPAY = "NOTPAY";
    public static final String PAY_STATUS_CLOSED = "CLOSED";
    public static final String PAY_STATUS_UNKNOW = "UNKNOW";

}
