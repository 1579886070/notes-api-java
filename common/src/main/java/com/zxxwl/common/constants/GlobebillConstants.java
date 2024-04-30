package com.zxxwl.common.constants;

public class GlobebillConstants {
    /**
     * tradeStatus	交易状态	string SUCCESS: 成功
     */
    public static final String TRADE_STATUS_SUCCESS = "SUCCESS";
    /**
     * tradeStatus	交易状态	string FAILURE: 失败
     */
    public static final String TRADE_STATUS_FAILURE = "FAILURE";
    /**
     * tradeStatus	交易状态	string UNKNOW: 未知
     */
    public static final String TRADE_STATUS_UNKNOW = "UNKNOW";
    /**
     * tradeType	交易类型	int		1-消费
     */
    public static final int TRADE_TYPE_EXPEND = 1;
    /**
     * tradeType	交易类型	int	 2-撤销 推测 取消支付
     */
    public static final int TRADE_TYPE_CANCEL = 2;
    /**
     * tradeType	交易类型	int	 3-退货 推测 退款
     */
    public static final int TRADE_TYPE_REFUND = 3;

}
