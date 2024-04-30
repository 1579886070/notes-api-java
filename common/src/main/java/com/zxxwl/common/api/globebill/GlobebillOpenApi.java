package com.zxxwl.common.api.globebill;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * 第三方支付-钱宝科技
 * <a href="https://ppmember.globebill.com/login.html">钱宝科技-后台|控制台</a>
 * <a href="https://ppopen.globebill.com/">钱宝科技APi 文档</a>
 * @author qingyu
 * @apiNote <p>
 *     暂不支持账户余额查询
 *     暂仅 支付有回调通知，退款无通知
 * </p>
 */
public interface GlobebillOpenApi {
    /**
     * 交易查询
     * <a href="https://ppopen.globebill.com/qrcode/query"></a>
     * @param orderSn orderSn
     * @return R
     */
    JsonNode query(String orderSn);

    JsonNode queryByTradeId(String tradeId);

    JsonNode pay(String orderSn, BigDecimal finalAmount, String notifyUrl, String appId, String userOpenId);

    JsonNode payWxMini(String orderSn, BigDecimal finalAmount, String notifyUrl, String appId, String userOpenId);

    JsonNode refund(String orderSn, BigDecimal amount);

    JsonNode refund(String refundSn, String orderSn, BigDecimal amount);

    JsonNode notify(JsonNode body);

    JsonNode send(String api, JsonNode bodyValue);
}
