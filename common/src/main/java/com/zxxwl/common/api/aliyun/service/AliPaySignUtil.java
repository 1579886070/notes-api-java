package com.zxxwl.common.api.aliyun.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.api.AlipayFundTransCommonApi;
import com.alipay.v3.api.AlipayTradeApi;
import com.alipay.v3.model.AlipayFundTransCommonQueryDefaultResponse;
import com.alipay.v3.model.AlipayFundTransCommonQueryResponseModel;
import com.alipay.v3.model.AlipayTradePayModel;
import com.alipay.v3.model.AlipayTradePayResponseModel;
import com.alipay.v3.util.model.AlipayConfig;
import com.zxxwl.common.constants.ALYConstants;
import com.zxxwl.common.random.IdUtils;
import com.zxxwl.config.JsonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import static com.zxxwl.common.constants.ALYConstants.*;

/**
 * AlipaySignUtil
 *
 * @apiNote 支付宝生成支付订单
 */
@Slf4j
public class AliPaySignUtil {


    private static final ObjectMapper objectMapper = JsonConfig.getInstance();

    private void t() throws ApiException {
        ApiClient apiClient = Configuration.getDefaultApiClient();
        // 设置网关地址
        apiClient.setBasePath(ALIPAY_SERVER_URL);
        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置应用ID
        alipayConfig.setAppId(ALIPAY_APP_ID);
        // 设置应用私钥
        alipayConfig.setPrivateKey(PRIVATE_KEY);
        // 设置支付宝公钥
        alipayConfig.setAlipayPublicKey(PUBLIC_KEY);
        apiClient.setAlipayConfig(alipayConfig);

        // 实例化客户端
        AlipayTradeApi api = new AlipayTradeApi();
        // 调用 alipay.trade.pay
        AlipayTradePayModel alipayTradePayModel = new AlipayTradePayModel()
                .outTradeNo("20210817010101001")
                .totalAmount("0.01")
                .subject("测试商品")
                .scene("bar_code")
                .authCode("28763443825664394");
        // 发起调用
        AlipayTradePayResponseModel response = api.pay(alipayTradePayModel);
    }
    /*
     */

    /**
     * 获取AlipayClient
     *
     * @return R
     */
    public synchronized static AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(ALYConstants.ALIPAY_SERVER_URL, ALYConstants.ALIPAY_APP_ID, ALYConstants.PRIVATE_KEY, "json", "UTF-8", ALYConstants.PUBLIC_KEY, "RSA2");
    }

    /**
     * 生成支付宝订单调用链接信息
     *
     * @param model  订单信息
     * @param notify 回调地址
     * @return R
     *//*
    public static String getAlipayOrderInfo(AlipayTradeAppPayModel model, String notify) {
        //防止加密时出现错误
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        request.setBizModel(model);
        //商户回调地址
        request.setNotifyUrl(notify);
        String orderInfo = null;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //response.getBody()就是orderString可以直接给客户端请求，无需再做处理。
            orderInfo = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        log.info("生成支付宝支付订单信息完成，order为：【{}】", orderInfo);

        return orderInfo;
    }

    *//**
     * 生成支付宝手机网站支付（H5）订单调用链接信息
     *
     * @param model  订单信息
     * @param notify 回调地址
     * @return R
     *//*
    public static String getAlipayH5OrderInfo(AlipayTradeAppPayModel model, String notify, String returnUrl) {
        //防止加密时出现错误
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        request.setBizModel(model);
        //商户回调地址
        request.setNotifyUrl(notify);
        request.setReturnUrl(returnUrl);
        String orderInfo = null;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
            //response.getBody()就是orderString可以直接给客户端请求，无需再做处理。
            orderInfo = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        log.info("生成支付宝H5支付订单信息完成，order为：【{}】", orderInfo);

        return orderInfo;
    }

    *//**
     * 支付宝退款
     *
     * @param orderId    订单id
     * @param payOrderId 交易id
     * @param price      价格（元）
     * @param reason     退款原因
     * @param notify     回调
     * @return R
     * @throws Exception
     *//*
    public static AlipayTradeRefundResponse refund(String orderId, String payOrderId, String price, String reason, String notify) throws Exception {
        log.info("【支付宝退款，退款订单id：[{}]，交易id：[{}]，退款价格：[{}]，退款原因：[{}]，回调地址：[{}]】", orderId, payOrderId, price, reason, notify);

        //防止加密时出现错误
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();
        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        Map<String, String> params = new TreeMap<>();
        // 必须 商户订单号
        params.put("out_trade_no", orderId);
        // 必须 支付宝交易号
        params.put("trade_no", payOrderId);
        // 必须 退款金额
        params.put("refund_amount", price);
        // 可选 代表 退款的原因说明
        params.put("refund_reason", reason);
        // 可选 标识一次退款请求，同一笔交易多次退款需要保证唯一（就是out_request_no在2次退款一笔交易时，要不一样），如需部分退款，则此参数必传
//        params.put("out_request_no", IdUtils.fastSimpleUUID());
        // 可选 代表 商户的门店编号
//        params.put("store_id", "90m");

        request.setBizContent(objectMapper.writeValueAsString(params));

        AlipayTradeRefundResponse responseData = alipayClient.execute(request);

        if (responseData.isSuccess()) {
            log.info("ali refund success tradeNo:{}", orderId);
        } else {
            log.error("【ali refund fail，info：[{}]】", responseData.getBody());
        }

        return responseData;
    }

    */

    /**
     * 支付宝提现/转账
     *
     * @param type          付款类型 0-支付宝的会员ID 1-支付宝账号与姓名
     * @param name          姓名
     * @param account       账号
     * @param userId        支付宝用户id
     * @param price         金额（元）
     * @param payerShowName 付款人显示名称
     * @param remark        备注
     * @return R
     */
    public static AlipayFundTransToaccountTransferResponse transfer(int type, String account, String name, String userId, String price, String payerShowName, String remark) throws Exception {
        log.info("【支付宝提现，账号：[{}]，姓名：[{}]，支付宝用户ID：[{}]，提价金额：[{}]，付款人显示名称：[{}]，备注：[{}]】", account, name, userId, price, payerShowName, remark);

        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();

        AlipayFundTransToaccountTransferModel transferModel = new AlipayFundTransToaccountTransferModel();

        String orderId = IdUtils.fastSimpleUUID();

        if (type == 0) {
            transferModel.setPayeeType("ALIPAY_USERID");
            transferModel.setPayeeAccount(userId);
        } else {
            transferModel.setPayeeType("ALIPAY_LOGONID");
            transferModel.setPayeeAccount(account);
            transferModel.setPayeeRealName(name);
        }

        transferModel.setOutBizNo(orderId);
        transferModel.setAmount(price);
        transferModel.setPayerShowName(payerShowName);
        transferModel.setRemark(remark);

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizModel(transferModel);
        AlipayFundTransToaccountTransferResponse responseData = alipayClient.execute(request);
        log.debug("{}",responseData);
        if (responseData.isSuccess()) {
            log.info("ali transfer success tradeNo:{}", orderId);
        } else {
            log.error("【ali transfer fail，info：[{}]】", responseData.getBody());
        }

        return responseData;
    }

    /**
     * 单笔转账
     * @apiNote <p><a href="https://opendocs.alipay.com/open/02byuo?pathHash=66064890&scene=ca56bca529e64125a2786703c6192d41&ref=api">单笔转账 采用此版本</a></p>
     * @apiNote <p><a href="https://opendocs.alipay.com/open-v3/62987723_alipay.fund.trans.uni.transfer?scene=ca56bca529e64125a2786703c6192d41&pathHash=25c36ba0">单笔转账v3</a></p>
     * @param data AlipayFundTransToaccountTransferModel
     * @return AlipayFundTransToaccountTransferResponse
     * @throws Exception
     */
    public static AlipayFundTransToaccountTransferResponse transfer(AlipayFundTransToaccountTransferModel data) throws Exception {

        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();


        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizModel(data);
        AlipayFundTransToaccountTransferResponse responseData = alipayClient.execute(request);
        log.debug("{}",responseData);
        if (responseData.isSuccess()) {
            log.info("ali transfer success tradeNo:{}", data.getOutBizNo());
        } else {
            log.error("【ali transfer fail，info：[{}]】", responseData.getBody());
        }

        return responseData;
    }

    public static void  query(){
        AlipayFundTransCommonApi api = new AlipayFundTransCommonApi();
        String productCode = "STD_RED_PACKET";
        String bizScene = "PERSONAL_PAY";
        String outBizNo = "201808080001";
        String orderId = "20190801110070000006380000250621";
        String payFundOrderId = "20190801110070001506380000251556";
        try {
            AlipayFundTransCommonQueryResponseModel response = api.query(productCode, bizScene, outBizNo, orderId, payFundOrderId);
        } catch (ApiException e) {
            AlipayFundTransCommonQueryDefaultResponse errorObject = (AlipayFundTransCommonQueryDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
    }
    /**
     * 换取授权访问令牌 access_token
     *
     * @param authCode 授权码
     * @return R
     *//*
    public static AlipaySystemOauthTokenResponse oauthToken(String authCode) {
        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();

        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);

        AlipaySystemOauthTokenResponse responseData = new AlipaySystemOauthTokenResponse();
        try {
            responseData = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("【oauthToken获取异常！】");
            e.printStackTrace();
        }

        if (responseData.isSuccess()) {
            log.info("ali auth token success authCode:{}", authCode);
        } else {
            log.error("【ali auth token fail，info：[{}]】", responseData.getBody());
        }

        return responseData;
    }

    *//**
     * 根据accessToken获取支付宝用户信息
     *
     * @param accessToken
     * @return R
     *//*
    public static AlipayUserInfoShareResponse getUserInfo(String accessToken) {
        //实例化客户端
        AlipayClient alipayClient = getAlipayClient();
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();//创建API对应的request类

        AlipayUserInfoShareResponse response = new AlipayUserInfoShareResponse();
        try {
            response = alipayClient.execute(request, accessToken);//在请求方法中传入上一步获得的access_token
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        if (response.isSuccess()) {
            log.info("ali user info share success authCode:{}", accessToken);
        } else {
            log.error("【ali user info share fail，info：[{}]】", response.getBody());
        }

        return response;
    }*/
}
