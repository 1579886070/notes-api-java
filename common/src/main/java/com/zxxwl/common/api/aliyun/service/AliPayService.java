package com.zxxwl.common.api.aliyun.service;

import com.alipay.v3.model.AlipayFundAccountQueryResponseModel;
import com.alipay.v3.model.AlipayFundTransUniTransferModel;
import com.alipay.v3.model.AlipayFundTransUniTransferResponseModel;

/**
 * <p>v2 v3 需要证书访问，原始模式不需要</p>
 * <p>{"code":"40002","msg":"Invalid Arguments","subCode":"isv.missing-app-cert-sn","subMsg":"缺少应用公钥证书序列号，解决方案：https://open.alipay.com/api/errCheck?traceId=21bb5edf16970012900852222e3bfb&source=openapi"}</p>
 */
public interface AliPayService {
    /**
     * 查询 余额
     *
     * @param alipayUserId alipayUserId
     */
    AlipayFundAccountQueryResponseModel query(String alipayUserId);

    /**
     * 转账-单笔免密
     * <a href="https://opendocs.alipay.com/open-v3/62987723_alipay.fund.trans.uni.transfer?scene=ca56bca529e64125a2786703c6192d41&pathHash=25c36ba0">单笔转账</a>
     *
     * @param data data
     * @apiNote <p>
     * 转账单据状态。 SUCCESS（该笔转账交易成功）：成功； FAIL：失败（具体失败原因请参见error_code以及fail_reason返回值）；
     * </p>
     */
    AlipayFundTransUniTransferResponseModel uniTransfer(AlipayFundTransUniTransferModel data);

    /**
     * 转账
     * <a href="https://opendocs.alipay.com/open-v3/62987723_alipay.fund.trans.uni.transfer?scene=ca56bca529e64125a2786703c6192d41&pathHash=25c36ba0">单笔转账</a>
     *
     * @param data data
     */
    AlipayFundTransUniTransferResponseModel transfer(AlipayFundTransUniTransferModel data);
}
