package com.zxxwl.common.api.pay;

import com.zxxwl.common.api.globebill.GlobebillOpenApi;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.zxxwl.common.constants.GlobebillConstants.*;
import static com.zxxwl.common.constants.PayConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {
    private final GlobebillOpenApi globebillOpenApi;
    // 支付方式类型：`WxMiniPay`=微信小程序，`Alipay`=支付宝，`Globebill`=钱宝科技-融合支付,`System`=后台系统设置）

    @Override
    public boolean checkPayByType7OrderSn(String platform, String orderSn) {
        String payStatus = queryPayStatusByPlatform7OrderSn(platform, orderSn);
        if (PAY_STATUS_SUCCESS.equals(payStatus)) {
            return true;
        }
        return false;
    }

    private String checkPayGlobebill(String orderSn) {
        JsonNode query = globebillOpenApi.query(orderSn);
        log.info("orderSn:{},query:{}", orderSn, query);
        // 	tradeStatus	交易状态	string  [SUCCESS: 成功,FAILURE: 失败,UNKNOW: 未知]
        switch (query.path("tradeType").asInt(0)) {
            case TRADE_TYPE_EXPEND -> {
                // 支出
                log.info("支出：{}", query.path("tradeStatus").asText(""));
                if (TRADE_STATUS_SUCCESS.equals(query.path("tradeStatus").asText(""))) {
                    // 付款成功支出成功，修改订单
                    // boolean paid = orderBaseService.upPaidByOrderSn(orderSn);
                    log.info("支付成功，orderSn：{}", orderSn);
                    return PAY_STATUS_SUCCESS;
                } else {
                    log.info("未支付成功，orderSn：{}", orderSn);

                }
            }
            case TRADE_TYPE_CANCEL -> {
                // 取消支付-暂不处理
                log.warn("取消支付-暂不处理 orderSn:{},tradeStatus:{}", orderSn, query.path("tradeStatus").asText(""));
            }
            case TRADE_TYPE_REFUND -> {
                // 退款-暂不处理
                log.warn("退款-暂不处理 orderSn:{},tradeStatus:{}", orderSn, query.path("tradeStatus").asText(""));
            }
            default -> {
                // boolean unknow = orderBaseService.upPayUnknowByOrderSn(orderSn);
                log.warn("异常状态处理 orderSn:{} handle:{}", orderSn, "unknow");
                return PAY_STATUS_UNKNOW;
            }
        }
        return PAY_STATUS_NOTPAY;
    }

    @Override
    public String queryPayStatusByPlatform7OrderSn(String platform, String orderSn) {
        //  support pay type
        if (Boolean.FALSE.equals(SUPPORT_PAY_PLATFORM.contains(platform))) {
            // 不受支持的
            throw new RuntimeException("不受支持的支付类型");
        }
        switch (platform) {
            case PAY_PLATFORM_GLOBEBILL -> {
                return checkPayGlobebill(orderSn);
            }
            case PAY_PLATFORM_ALIPAY -> {
                return PAY_STATUS_UNKNOW;
            }
            default -> {
                return PAY_STATUS_UNKNOW;
            }
        }
    }
}
