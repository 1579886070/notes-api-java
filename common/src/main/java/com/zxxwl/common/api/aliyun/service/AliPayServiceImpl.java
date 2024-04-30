package com.zxxwl.common.api.aliyun.service;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.api.AlipayFundAccountApi;
import com.alipay.v3.api.AlipayFundTransUniApi;
import com.alipay.v3.model.*;
import com.alipay.v3.util.model.AlipayConfig;
import com.zxxwl.common.api.sys.thirdaccount.service.SysThirdAccountService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static com.zxxwl.common.constants.AliPayConstants.BIZSCENE_DIRECT_TRANSFER;
import static com.zxxwl.common.constants.AliPayConstants.PRODUCTCODE_TRANS_ACCOUNT_NO_PWD;
import static com.zxxwl.common.constants.SysThirdAccountContents.PAY_ALIPAY;

@Slf4j
@RequiredArgsConstructor
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AliPayServiceImpl implements AliPayService {
    // test
    //    private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    /*private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com";
    private static final String ALIPAY_APP_ID = "9021000129622545";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQLa0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpugdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijpmqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMBAAECggEAC2Dnce2YyUFL4uXy588s1VudjRNRZhryrMZ15rdg7SvVWrZ0EI0PEuYa5Fx4XL71ZyNjGaM/gG6VQJsXtjew8jiiH7ZNWQiH05l67EUJ2E/5337LuhIQvzFf1RYjpz4nl3VJT5ecAEk0CrfoCewXI+D4l0YYy+S+SEQc31418QU794LfYW/7IbR96cAFyKBM/6FDqUd0SjVDJbYL7gBDk+vNhjdtBmxsbWVROZ7mUziXrkRvlgJ3Z0BJCR179WguQanUEdhDMf6y3sSfpdupp7LuO/BzDn734slvF0uHcK6bnBdiGb+QugCizhI/V0hxobNNLTH+Fl+VWTTb0w3YAQKBgQC51HjsfKIRHr4Ac6wMqeC3OgZRj2/VUtTbDC3rx9AvZ8S8DExUxqv0S2Z2+SCGh/WQ7epYLf/lglr1Q0PdABWV41MswEQ9XspnrdaZVZncTjNZIkwNZE/Lpl2ybapL5LxLz0deJHYEU7zio8NGfw7SnjZuZb4RuWW2voMhCnDxgQKBgQC4TU8XUxs+uVyH+cYjfgMigo7seUtQNaLS1uvsKpPACkUdoTW5T9dXV3/a+M9n7l/UPTBfLc5kcpzfIzUPHsDu4GAHmZMjecxXBxt+PYCoFH4YDgTtAb1SaCrEaOzyR4bsXIMx02sMihcmRKUzEsSSJi2lQAQm2Fp+LBe+a0ZYGQKBgA5xq2yGS3qVLyON2iOp2KOlSPRx97lXFOBp0+/tugrVP/913UEBqD/21GIO/y41xyqas4pjGO7X8QX7Un0NEqBx6PcjgqpagdUJOBL3ClyLX4ZgGhU4ZNNpfQLvbzsy7kfeNqfkoZ+4eogLPpRuL9LDJWvzUJDEHlnT+RFKZHkBAoGBAIJ2d15Tfh3QqSe4MAI/2Yg8U2AiHKYS0cfeS/NJ0H1Ix9RJofYkBpPVZLRlHKy9mShsiClYC5ofZ9ys14p0gq6WNmQq2nVqarPQXC3o64IPv5LXmbUMXjBPfLa8b76x01jNxDEpL8YYmnqEbUzSJEQwsm8f+dBlpulZ3RT2az1BAoGAfM6SqpEHQicmtMZedDYDCKWSWrIApgthApJ6M/DnI2tFuX0Bu8SgIm+PiyhNB4jkvLWaadiHZpXvjL+4BbWaedcDy3Nql1js21oAAuJZ/P14VZyadrxWlCh/GB2jnC3JgXEeeTpvMQgIvQkxF1tyDzlcjmjIw04prU/9nXk6pOQ=";
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgfBvwMX8QwrFQUmtkfnDsEfNqIk6NPi67VWhXRm3XtYQ0h82SlSJXylfL2SxMJXRk2s3wYbeZJUdPNbpuBTLdfBV8NUPX3QQ882OXrCfGIRZxHXg70Xq+Hn3JHHoA3ldyC7rCAyPAmycGApmQbj9gR3k9j8pKiUFyt8XAZlpwHJOr/KWmeTgtIfFTcdtZXx0RESt2fVk9mok7W0JXBdDC0zmVO692V8sCEX21F3z/2bSujEIttqDICMrHUe4cPct5xHdz96P/HNLUnys+QZsB11GoBHdEDqqe7M9g6SHIJ/tmmz5Od/n9CBTr32IEQpWs+TrfYgSZagyK2tCve6mwQIDAQAB";
    */
    private static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com";
    /*    private static final String ALIPAY_APP_ID = "2021002133634470";
        private static final String PRIVATE_KEY = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDQoGWJhzt23AnYQI0rjArL0S6sdi7NgSssqVVIutpuF6hmGumwzmyd5NyRuxC3CPBbyHvOGj6L4eZ5hfjP77BLsxVP54fSYKM+Lu4Hd8XeFykUnTFsFi240cVe8yxzR2b7DOKa2q2ACsowkAww5pLow2LjUGIbTaDxMmm1vCmnI53LUFK3rawJN2C58qiBBrPTnRTXHdRKnfsq+12uE+AD4NfgRsuM1pNjbPRBniJ/WG/3R82zDwjsk1sWjrv2s8BuTO2Dh/I8bjdCMXcrO7Zqncuh8I17AYittGl5nWPtgsqmp8adSCfu9dhYmfWUi+r5StADMlxbTKVfp8zoa3eXAgMBAAECggEBALy6POBau4pcp3M/gfWA7EtQ4l4VU2WIkhwG2yW+EtdBA4/KjNjrBAsZ/AbBKO/LYFRHWugpiadVJf+NGHZSqDLywDbWhmHkFgisv5t12EN+xCTnxsu2jIoDCf9JEE8AqsI099GKPSX1nM6toLmj/AYgqpZkIzv7z7oiFsrWev+8bwz/b4yRexlnAhCwcUID3tc032/dvZa68vlTbiLsU6ZgNuvDF/ysD0Q08oBFlvsA/EbqSMxyIabiI6tv/UxsfM3yKllpRhFIxFPwTKMpQ1kFBK+ahCIl3coOGOerHc4Fn9ZcrclZAfuVxE9/kDG9NmkmnKB4GRjfe3ARP7e+sWECgYEA+l3sZX6xPMaGA8ssKAKQKmY/hHyderV0XEsEbX2JbCSvfrnUGpb3wi4dp8Ek6jNsZretVBCDgAosOOkyE8sy5Wj4zgTq2og5Vpn72qvf3GVoY0kcvr/zxafd5ai5rCYOZl7fUO9P1dti4uk7f1itCIvVeLUqhZZyyTo1fWtQKs8CgYEA1VIOEqJZyJ+DYMklTPgSXyGuDudeTwtqFyPZ7FqJRFpciCOqyKiuQbWomgpHh++nbxyZT3zTwg0OtvY4ziZUzCf9X0G7Eh0svnjqWF6eqIjE+bc/1DzWTRAHby08O0jnVr9oejaiBd5yIiOAWetKK454QtC3tw2Qgx5e3x68+LkCgYEAko9Avvk3D3i3Iqlgo9s8QVWxgQDhvt9RymUvpliChYfEI87sVtRhHI6aVYU+8so/frGvCTLRh7ZsxLPd2LBujtSwMQm34U01I3jocc2DMiU6wkQeS5taPC5UOwhWCGwOqeaHm307otjWPjaB2b4zLHmO8SY8Sg8UCq/EeDLFrwkCgYEAq0/dW1ukTPjFUjnXScvDPUDX/sISO9qH1ZOGFEjVCd6jhvVV1xWX889GWoa80U9GIHZQLMSrbqB727U7A1tHKDlJQnGQIUjqwQBr+pHqSqCjx4NBsFk3JOYlgkRKpbWzizfmWJd9YMc203G9RvktNHOS4t2pXEl5b8M5H2gZDIkCgYEAty7ODEA4QpzJgUfLIvvX2RkPWgeRRp6FwuLQntytGbzEOtb5PXH5SOhI69POzX92oje+jzGpXOrDWKAsms8BNX/9WJE+qv80EjVMqi8GWekLs4dc7e28RiEnuiB5pglBjuD1so9/hTc5iUcyyeTWPJknOpaEVlnjmfTkfxKV2m4=";
        private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuV0oifOMcafV4IZWAgAXdnpwSCrUlfLfuP/wcnFB6IOqtkGhro2AwzqCDYekwjuApsSZU4MWjCwBdURUP0kOdSx4uIWfyrtwFFC4k6Q2zJmL44bQ+M0bmTRm8TWIqn1lfiXMJAqcdz5YX2UB1bdJa8adVpRVaZoHwQD/65stOy4PpdvDCbcXt0PXaLENJnS+gVUt7T9A3yr6lgKbRDdBfdEhszzF+rEOv9xQGJ59f5k+xSBihriJ0PtHs7g7PShv5EKVvKn8sRjbnV9IuaTf88j2qFz2LaIIvv+HzuAgqE7p23UGo9vDb95JzzL/ph4SHE6hxZWP9SC8qPLV5EfdEQIDAQAB";
       */
    private static final ApiClient apiClient = Configuration.getDefaultApiClient();
    //    private final SysThirdAccountService sysThirdAccountService;
    private final SysThirdAccountService sysThirdAccountService;

    @PostConstruct
    private void init() {
        JsonNode jsonNode = sysThirdAccountService.queryJsonContentByDataId(PAY_ALIPAY);
        log.info("alipay-init:{}", jsonNode);

        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置应用ID
        alipayConfig.setAppId(jsonNode.path("appid").asText());
        // 设置应用私钥
        alipayConfig.setPrivateKey(jsonNode.path("private_key").asText());
        // 设置支付宝公钥
        alipayConfig.setAlipayPublicKey(jsonNode.path("public_key").asText());
        alipayConfig.setEncryptType("RSA2");
        apiClient.setBasePath(ALIPAY_SERVER_URL);
        try {
            apiClient.setAlipayConfig(alipayConfig);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public AlipayFundAccountQueryResponseModel query(String alipayUserId) {
        AlipayFundAccountApi api = new AlipayFundAccountApi();
        String merchantUserId = null;
        String alipayOpenId = null;
        String accountProductCode = null;
        String accountType = "ACCTRANS_ACCOUNT";
        String accountSceneCode = null;
        String extInfo = null;
        AlipayFundAccountQueryResponseModel response = null;
        try {
            response = api.query(merchantUserId, alipayUserId, alipayOpenId, accountProductCode, accountType, accountSceneCode, extInfo);
            log.info("{}", response);
        } catch (ApiException e) {
            log.error("", e);
            AlipayFundAccountQueryDefaultResponse errorObject = (AlipayFundAccountQueryDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
        return response;
    }

    @Override
    public AlipayFundTransUniTransferResponseModel uniTransfer(AlipayFundTransUniTransferModel data) {
        // 业务场景。单笔无密转账固定为 DIRECT_TRANSFER。
        data.setBizScene(BIZSCENE_DIRECT_TRANSFER);
        // 销售产品码。单笔无密转账固定为 TRANS_ACCOUNT_NO_PWD。
        data.setProductCode(PRODUCTCODE_TRANS_ACCOUNT_NO_PWD);
        return this.transfer(data);
    }

    @Override
    public AlipayFundTransUniTransferResponseModel transfer(AlipayFundTransUniTransferModel data) {
        AlipayFundTransUniApi api = new AlipayFundTransUniApi();
        AlipayFundTransUniTransferResponseModel response = null;
        try {
            response = api.transfer(data);
            log.info("{}", response);
        } catch (ApiException e) {
            AlipayFundTransUniTransferDefaultResponse errorObject = (AlipayFundTransUniTransferDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
        return response;
    }
}
