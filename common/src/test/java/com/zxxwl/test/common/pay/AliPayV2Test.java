package com.zxxwl.test.common.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.zxxwl.common.api.aliyun.service.AliPaySignUtil;
import com.zxxwl.common.constants.ALYConstants;
import com.zxxwl.common.random.IdUtils;
import com.zxxwl.config.JsonConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;

@Slf4j
public class AliPayV2Test {
    //    private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    /*private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com";
    private static final String ALIPAY_APP_ID = "9021000129622545";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQLa0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpugdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijpmqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMBAAECggEAC2Dnce2YyUFL4uXy588s1VudjRNRZhryrMZ15rdg7SvVWrZ0EI0PEuYa5Fx4XL71ZyNjGaM/gG6VQJsXtjew8jiiH7ZNWQiH05l67EUJ2E/5337LuhIQvzFf1RYjpz4nl3VJT5ecAEk0CrfoCewXI+D4l0YYy+S+SEQc31418QU794LfYW/7IbR96cAFyKBM/6FDqUd0SjVDJbYL7gBDk+vNhjdtBmxsbWVROZ7mUziXrkRvlgJ3Z0BJCR179WguQanUEdhDMf6y3sSfpdupp7LuO/BzDn734slvF0uHcK6bnBdiGb+QugCizhI/V0hxobNNLTH+Fl+VWTTb0w3YAQKBgQC51HjsfKIRHr4Ac6wMqeC3OgZRj2/VUtTbDC3rx9AvZ8S8DExUxqv0S2Z2+SCGh/WQ7epYLf/lglr1Q0PdABWV41MswEQ9XspnrdaZVZncTjNZIkwNZE/Lpl2ybapL5LxLz0deJHYEU7zio8NGfw7SnjZuZb4RuWW2voMhCnDxgQKBgQC4TU8XUxs+uVyH+cYjfgMigo7seUtQNaLS1uvsKpPACkUdoTW5T9dXV3/a+M9n7l/UPTBfLc5kcpzfIzUPHsDu4GAHmZMjecxXBxt+PYCoFH4YDgTtAb1SaCrEaOzyR4bsXIMx02sMihcmRKUzEsSSJi2lQAQm2Fp+LBe+a0ZYGQKBgA5xq2yGS3qVLyON2iOp2KOlSPRx97lXFOBp0+/tugrVP/913UEBqD/21GIO/y41xyqas4pjGO7X8QX7Un0NEqBx6PcjgqpagdUJOBL3ClyLX4ZgGhU4ZNNpfQLvbzsy7kfeNqfkoZ+4eogLPpRuL9LDJWvzUJDEHlnT+RFKZHkBAoGBAIJ2d15Tfh3QqSe4MAI/2Yg8U2AiHKYS0cfeS/NJ0H1Ix9RJofYkBpPVZLRlHKy9mShsiClYC5ofZ9ys14p0gq6WNmQq2nVqarPQXC3o64IPv5LXmbUMXjBPfLa8b76x01jNxDEpL8YYmnqEbUzSJEQwsm8f+dBlpulZ3RT2az1BAoGAfM6SqpEHQicmtMZedDYDCKWSWrIApgthApJ6M/DnI2tFuX0Bu8SgIm+PiyhNB4jkvLWaadiHZpXvjL+4BbWaedcDy3Nql1js21oAAuJZ/P14VZyadrxWlCh/GB2jnC3JgXEeeTpvMQgIvQkxF1tyDzlcjmjIw04prU/9nXk6pOQ=";
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgfBvwMX8QwrFQUmtkfnDsEfNqIk6NPi67VWhXRm3XtYQ0h82SlSJXylfL2SxMJXRk2s3wYbeZJUdPNbpuBTLdfBV8NUPX3QQ882OXrCfGIRZxHXg70Xq+Hn3JHHoA3ldyC7rCAyPAmycGApmQbj9gR3k9j8pKiUFyt8XAZlpwHJOr/KWmeTgtIfFTcdtZXx0RESt2fVk9mok7W0JXBdDC0zmVO692V8sCEX21F3z/2bSujEIttqDICMrHUe4cPct5xHdz96P/HNLUnys+QZsB11GoBHdEDqqe7M9g6SHIJ/tmmz5Od/n9CBTr32IEQpWs+TrfYgSZagyK2tCve6mwQIDAQAB";
    */
    private static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com";
    private static final AlipayClient apiClient = new DefaultAlipayClient(ALYConstants.ALIPAY_SERVER_URL, ALYConstants.ALIPAY_APP_ID, ALYConstants.PRIVATE_KEY, "json", "UTF-8", ALYConstants.PUBLIC_KEY, "RSA2");


/*    static {
        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置应用ID
        alipayConfig.setAppId(ALIPAY_APP_ID);
        // 设置应用私钥
        alipayConfig.setPrivateKey(PRIVATE_KEY);
        // 设置支付宝公钥
        alipayConfig.setAlipayPublicKey(PUBLIC_KEY);
        try {
            apiClient.setBasePath(ALIPAY_SERVER_URL);
            apiClient.setAlipayConfig(alipayConfig);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }*/

    /**
     * {"code":"10000","msg":"Success","subCode":null,"subMsg":null,"body":"{\"alipay_fund_trans_toaccount_transfer_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"order_id\":\"20231011110070001506430073323694\",\"out_biz_no\":\"853558402c9249598e21bd99aa4b57cc\",\"pay_date\":\"2023-10-11 13:03:54\"},\"sign\":\"TYQCXhW+Qf+SBonv/aGLnl9FiTWHN8MzR+wbbi/JjWC5fKcCTKuKQmhVN3PCz4Dz1oxG6fJfDm/lUlu4l/uOzllchHlnZeoAQ7EaVjiTcekOLF5QtQaTVqqxy0TV+L+ZnZInJyH6GMwAhvJLRedSHf5cUOd8e2XovJ42h+F1+ww722YDL6iZWnpuh2sNPv2VnuH7ci84LCoSIyKXCOUr2XANqSB46Fwjr5P8joBzxWJCdit7AnBXFDK3RzpDjMvtXhUgaBZtIobp8cO95u40T7Y8AR/cOgOiSfPcNgsMQ0lzgJeWlSDxevDPCafz8O7h3rOMGmH8eoNsZqIOOrtqQQ==\"}","params":{"biz_content":"{\"amount\":\"0.10\",\"out_biz_no\":\"853558402c9249598e21bd99aa4b57cc\",\"payee_account\":\"15868372831\",\"payee_real_name\":\"李宁\",\"payee_type\":\"ALIPAY_LOGONID\",\"payer_show_name\":\"\",\"remark\":\"test\"}"},"orderId":"20231011110070001506430073323694","outBizNo":"853558402c9249598e21bd99aa4b57cc","payDate":"2023-10-11 13:03:54","success":true,"errorCode":"10000"}
     */
    @SneakyThrows
    @Test
    public void tOld() {
        //实例化客户端

        AlipayFundTransToaccountTransferModel transferModel = new AlipayFundTransToaccountTransferModel();
        String orderId = IdUtils.fastSimpleUUID();

        transferModel.setPayeeType("ALIPAY_LOGONID");
        transferModel.setPayeeAccount("15868372831");
        transferModel.setPayeeRealName("李宁");
        transferModel.setOutBizNo(orderId);
        transferModel.setAmount("0.10");
        transferModel.setPayerShowName("");
        transferModel.setRemark("test");

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizModel(transferModel);

        AlipayFundTransToaccountTransferResponse response = apiClient.execute(request);

        log.info("{}", JsonConfig.getInstance().writeValueAsString(response));
        if (response.isSuccess()) {
            log.info("ali transfer success tradeNo:{}", orderId);
        } else {
            log.error("【ali transfer fail，info：[{}]】", response.getBody());
        }
    }

    /**
     * {"code":"40002","msg":"Invalid Arguments","subCode":"isv.missing-app-cert-sn","subMsg":"缺少应用公钥证书序列号，解决方案：https://open.alipay.com/api/errCheck?traceId=21bb5edf16970012900852222e3bfb&source=openapi","body":"{\"alipay_fund_trans_uni_transfer_response\":{\"code\":\"40002\",\"msg\":\"Invalid Arguments\",\"sub_code\":\"isv.missing-app-cert-sn\",\"sub_msg\":\"缺少应用公钥证书序列号，解决方案：https://open.alipay.com/api/errCheck?traceId=21bb5edf16970012900852222e3bfb&source=openapi\"},\"sign\":\"YIKu2CbQLRo6Lslxl23v135zrssuUd0GOai4ebLAlQg/w1+EEhTMX/JY/d6t6KaWsRT5EIOqGOYhoGRKmt1jkqXhJgOgy5wcY3Nden+ixjzUsu5L5CdICpEvsHfMmA/Z5DrRlYSENFLnjn2Tx1FGBDSMKFD3U7SeUvF8ZS8uHF/fY7Xt88RCXCE2oBJafcUCo8TpndDKXdJRTeR11qKHvac5N2SDbR1tWWoGPFceb+j/nf1E5qALWvnr2Wo7JNg8POf8xVgXmmQOox4O56A8bCyPv4yMM+ttbLziwnOfn6k/G6HLsfdjTMzsViIg52MoGY10T0WYnLKPef3rhG/Z+A==\"}","params":{"biz_content":"{}"},"orderId":null,"outBizNo":null,"payFundOrderId":null,"settleSerialNo":null,"status":null,"transDate":null,"success":false,"errorCode":"40002"}
     */
    @SneakyThrows
    @Test
    public void v2() {
        //实例化客户端

        AlipayFundTransToaccountTransferModel transferModel = new AlipayFundTransToaccountTransferModel();
        String orderId = IdUtils.fastSimpleUUID();
        AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
        model.setOutBizNo(orderId);
        model.setBizScene("DIRECT_TRANSFER");
        model.setTransAmount("0.10");
        Participant payeeInfo = new Participant();
        payeeInfo.setIdentity("15868372831");
        payeeInfo.setIdentityType("ALIPAY_LOGONID");
        payeeInfo.setName("李宁");
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        request.setBizModel(transferModel);


        AlipayFundTransUniTransferResponse response = apiClient.certificateExecute(request);

        log.info("{}", response.getStatus());
        log.info("{}", response.getSubMsg());
        log.info("{}", JsonConfig.getInstance().writeValueAsString(response));
        if (response.isSuccess()) {
            log.info("ali transfer success tradeNo:{}", orderId);
        } else {
            log.error("【ali transfer fail，info：[{}]】", response.getBody());
        }
    }

    /**
     * {"code":"10000","msg":"Success","subCode":null,"subMsg":null,"body":"{\"alipay_fund_trans_toaccount_transfer_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"order_id\":\"20231027110070001506430081405302\",\"out_biz_no\":\"1698393248218\",\"pay_date\":\"2023-10-27 15:54:09\"},\"sign\":\"dZlzwHYM6tZRdDpXWnzRc1U5UpvcECJvUv3N/T0N9iX2s13Bl49VqhoOOCnY1g7ai9mJqGjsNelOFI3ThbACnZ9YKiR3W17iD1vUg0vSxo+oajy3/IgpA6Uj9ClY2fYek8SMWpvkhgiDeeNPetxnb3FW4x6rx+xFnncwihxUQICw0ExkXLqmdX+TJiMSfomoXC6D9ZeqMF7M9YDMh5JR1oKGA7By6cdI4Z7zAh7ChaNcGY8EZ+RX7UZ9JShbpq+Acg2hKosGiKbdeYgjDvedBkizEw5qT3uDvSv4h52MA2XZ+a+LQgrQae5XiZ+dUhQHO4vzfIXqdSDvuXylDeuCPg==\"}","params":{"biz_content":"{\"amount\":\"0.1\",\"out_biz_no\":\"1698393248218\",\"payee_account\":\"15868372831\",\"payee_real_name\":\"李宁\",\"payee_type\":\"ALIPAY_LOGONID\",\"payer_show_name\":\"test PayerShowName\",\"remark\":\"test Remark\"}"},"orderId":"20231027110070001506430081405302","outBizNo":"1698393248218","payDate":"2023-10-27 15:54:09","success":true,"errorCode":"10000"}
     */
    @Test
    public void t() {
        AlipayFundTransToaccountTransferModel data = new AlipayFundTransToaccountTransferModel();
        String id = String.valueOf(Instant.now().toEpochMilli());
        log.debug("id:{}", id);
        data.setOutBizNo(id);
        data.setRemark("test Remark");
        data.setAmount("0.1");
        data.setPayerShowName("test PayerShowName");
        // data.setBusinessParams("{\"payer_show_name_use_alias\":\"true\"}");

        data.setPayeeAccount("15868372831");
        data.setPayeeType("ALIPAY_LOGONID");
        data.setPayeeRealName("李宁1");
        try {
            log.info("{}", JsonConfig.getInstance().writeValueAsString(data));
            AlipayFundTransToaccountTransferResponse transResp = AliPaySignUtil.transfer(data);

            log.info("{}", JsonConfig.getInstance().writeValueAsString(transResp));
            log.info("msg:{}", transResp.getMsg());
            log.info("getBody:{}", transResp.getBody());
            log.info("getSubMsg:{}", transResp.getSubMsg());
            log.info("getSubCode:{}", transResp.getSubCode());
            log.info("isSuccess:{}", transResp.isSuccess());
        } catch (Exception e) {

        }
    }
}
