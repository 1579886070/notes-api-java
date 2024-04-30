package com.zxxwl.common.constants;

public class AliPayConstants {
    /**
     * 参与方的标识类型，目前支持如下枚举：
     * 枚举值
     * 支付宝的会员ID: ALIPAY_USER_ID 支付宝会员的用户 ID，可通过 获取会员信息 能力获取。
     * 支付宝登录号: ALIPAY_LOGON_ID 支付宝登录号，支持邮箱和手机号格式。
     * 支付宝openid: ALIPAY_OPEN_ID
     */
    public static final String ID_TYPE_ALIPAY_USER_ID = "ALIPAY_USER_ID";
    public static final String ID_TYPE_ALIPAY_LOGON_ID = "ALIPAY_LOGON_ID";
    public static final String ID_TYPE_ALIPAY_OPEN_ID = "ALIPAY_OPEN_ID";
    public static final String BIZSCENE_DIRECT_TRANSFER = "";
    public static final String PRODUCTCODE_TRANS_ACCOUNT_NO_PWD = "";
    public static final String STATUS_SUCCESS = "SUCCESS";
    /**
     * 余额不足 <a href="https://opendocs.alipay.com/open/62987723_alipay.fund.trans.uni.transfer?pathHash=66064890&ref=api&scene=ca56bca529e64125a2786703c6192d41"></a>
     */
    public static final String BALANCE_IS_NOT_ENOUGH = "";
    public static final String PAYEE_TYPE_ALIPAY_LOGONID = "";

}
