package com.zxxwl.common.api.pay;

public interface PayService {
    /**
     * 根据支付方式，订单sn 校验支付状态
     *
     * @param platform 支付平台|交易平台
     * @param orderSn  订单状态
     * @return ok
     */
    boolean checkPayByType7OrderSn(String platform, String orderSn);

    String queryPayStatusByPlatform7OrderSn(String platform, String orderSn);
}
