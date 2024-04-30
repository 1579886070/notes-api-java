package com.zxxwl.test.common.pay;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.api.AlipayFundAccountApi;
import com.alipay.v3.api.AlipayFundTransUniApi;
import com.alipay.v3.model.*;
import com.alipay.v3.util.model.AlipayConfig;
import com.zxxwl.common.random.IdUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class AliPayV3Test {
    //    private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    /*private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com";
    private static final String ALIPAY_APP_ID = "9021000129622545";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQLa0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpugdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijpmqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMBAAECggEAC2Dnce2YyUFL4uXy588s1VudjRNRZhryrMZ15rdg7SvVWrZ0EI0PEuYa5Fx4XL71ZyNjGaM/gG6VQJsXtjew8jiiH7ZNWQiH05l67EUJ2E/5337LuhIQvzFf1RYjpz4nl3VJT5ecAEk0CrfoCewXI+D4l0YYy+S+SEQc31418QU794LfYW/7IbR96cAFyKBM/6FDqUd0SjVDJbYL7gBDk+vNhjdtBmxsbWVROZ7mUziXrkRvlgJ3Z0BJCR179WguQanUEdhDMf6y3sSfpdupp7LuO/BzDn734slvF0uHcK6bnBdiGb+QugCizhI/V0hxobNNLTH+Fl+VWTTb0w3YAQKBgQC51HjsfKIRHr4Ac6wMqeC3OgZRj2/VUtTbDC3rx9AvZ8S8DExUxqv0S2Z2+SCGh/WQ7epYLf/lglr1Q0PdABWV41MswEQ9XspnrdaZVZncTjNZIkwNZE/Lpl2ybapL5LxLz0deJHYEU7zio8NGfw7SnjZuZb4RuWW2voMhCnDxgQKBgQC4TU8XUxs+uVyH+cYjfgMigo7seUtQNaLS1uvsKpPACkUdoTW5T9dXV3/a+M9n7l/UPTBfLc5kcpzfIzUPHsDu4GAHmZMjecxXBxt+PYCoFH4YDgTtAb1SaCrEaOzyR4bsXIMx02sMihcmRKUzEsSSJi2lQAQm2Fp+LBe+a0ZYGQKBgA5xq2yGS3qVLyON2iOp2KOlSPRx97lXFOBp0+/tugrVP/913UEBqD/21GIO/y41xyqas4pjGO7X8QX7Un0NEqBx6PcjgqpagdUJOBL3ClyLX4ZgGhU4ZNNpfQLvbzsy7kfeNqfkoZ+4eogLPpRuL9LDJWvzUJDEHlnT+RFKZHkBAoGBAIJ2d15Tfh3QqSe4MAI/2Yg8U2AiHKYS0cfeS/NJ0H1Ix9RJofYkBpPVZLRlHKy9mShsiClYC5ofZ9ys14p0gq6WNmQq2nVqarPQXC3o64IPv5LXmbUMXjBPfLa8b76x01jNxDEpL8YYmnqEbUzSJEQwsm8f+dBlpulZ3RT2az1BAoGAfM6SqpEHQicmtMZedDYDCKWSWrIApgthApJ6M/DnI2tFuX0Bu8SgIm+PiyhNB4jkvLWaadiHZpXvjL+4BbWaedcDy3Nql1js21oAAuJZ/P14VZyadrxWlCh/GB2jnC3JgXEeeTpvMQgIvQkxF1tyDzlcjmjIw04prU/9nXk6pOQ=";
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgfBvwMX8QwrFQUmtkfnDsEfNqIk6NPi67VWhXRm3XtYQ0h82SlSJXylfL2SxMJXRk2s3wYbeZJUdPNbpuBTLdfBV8NUPX3QQ882OXrCfGIRZxHXg70Xq+Hn3JHHoA3ldyC7rCAyPAmycGApmQbj9gR3k9j8pKiUFyt8XAZlpwHJOr/KWmeTgtIfFTcdtZXx0RESt2fVk9mok7W0JXBdDC0zmVO692V8sCEX21F3z/2bSujEIttqDICMrHUe4cPct5xHdz96P/HNLUnys+QZsB11GoBHdEDqqe7M9g6SHIJ/tmmz5Od/n9CBTr32IEQpWs+TrfYgSZagyK2tCve6mwQIDAQAB";
    */
    /**
     * MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQLa0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpugdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijpmqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMBAAECggEAC2Dnce2YyUFL4uXy588s1VudjRNRZhryrMZ15rdg7SvVWrZ0EI0PEuYa5Fx4XL71ZyNjGaM/gG6VQJsXtjew8jiiH7ZNWQiH05l67EUJ2E/5337LuhIQvzFf1RYjpz4nl3VJT5ecAEk0CrfoCewXI+D4l0YYy+S+SEQc31418QU794LfYW/7IbR96cAFyKBM/6FDqUd0SjVDJbYL7gBDk+vNhjdtBmxsbWVROZ7mUziXrkRvlgJ3Z0BJCR179WguQanUEdhDMf6y3sSfpdupp7LuO/BzDn734slvF0uHcK6bnBdiGb+QugCizhI/V0hxobNNLTH+Fl+VWTTb0w3YAQKBgQC51HjsfKIRHr4Ac6wMqeC3OgZRj2/VUtTbDC3rx9AvZ8S8DExUxqv0S2Z2+SCGh/WQ7epYLf/lglr1Q0PdABWV41MswEQ9XspnrdaZVZncTjNZIkwNZE/Lpl2ybapL5LxLz0deJHYEU7zio8NGfw7SnjZuZb4RuWW2voMhCnDxgQKBgQC4TU8XUxs+uVyH+cYjfgMigo7seUtQNaLS1uvsKpPACkUdoTW5T9dXV3/a+M9n7l/UPTBfLc5kcpzfIzUPHsDu4GAHmZMjecxXBxt+PYCoFH4YDgTtAb1SaCrEaOzyR4bsXIMx02sMihcmRKUzEsSSJi2lQAQm2Fp+LBe+a0ZYGQKBgA5xq2yGS3qVLyON2iOp2KOlSPRx97lXFOBp0+/tugrVP/913UEBqD/21GIO/y41xyqas4pjGO7X8QX7Un0NEqBx6PcjgqpagdUJOBL3ClyLX4ZgGhU4ZNNpfQLvbzsy7kfeNqfkoZ+4eogLPpRuL9LDJWvzUJDEHlnT+RFKZHkBAoGBAIJ2d15Tfh3QqSe4MAI/2Yg8U2AiHKYS0cfeS/NJ0H1Ix9RJofYkBpPVZLRlHKy9mShsiClYC5ofZ9ys14p0gq6WNmQq2nVqarPQXC3o64IPv5LXmbUMXjBPfLa8b76x01jNxDEpL8YYmnqEbUzSJEQwsm8f+dBlpulZ3RT2az1BAoGAfM6SqpEHQicmtMZedDYDCKWSWrIApgthApJ6M/DnI2tFuX0Bu8SgIm+PiyhNB4jkvLWaadiHZpXvjL+4BbWaedcDy3Nql1js21oAAuJZ/P14VZyadrxWlCh/GB2jnC3JgXEeeTpvMQgIvQkxF1tyDzlcjmjIw04prU/9nXk6pOQ=
     */
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQLa0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpugdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijpmqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMBAAECggEAC2Dnce2YyUFL4uXy588s1VudjRNRZhryrMZ15rdg7SvVWrZ0EI0PEuYa5Fx4XL71ZyNjGaM/gG6VQJsXtjew8jiiH7ZNWQiH05l67EUJ2E/5337LuhIQvzFf1RYjpz4nl3VJT5ecAEk0CrfoCewXI+D4l0YYy+S+SEQc31418QU794LfYW/7IbR96cAFyKBM/6FDqUd0SjVDJbYL7gBDk+vNhjdtBmxsbWVROZ7mUziXrkRvlgJ3Z0BJCR179WguQanUEdhDMf6y3sSfpdupp7LuO/BzDn734slvF0uHcK6bnBdiGb+QugCizhI/V0hxobNNLTH+Fl+VWTTb0w3YAQKBgQC51HjsfKIRHr4Ac6wMqeC3OgZRj2/VUtTbDC3rx9AvZ8S8DExUxqv0S2Z2+SCGh/WQ7epYLf/lglr1Q0PdABWV41MswEQ9XspnrdaZVZncTjNZIkwNZE/Lpl2ybapL5LxLz0deJHYEU7zio8NGfw7SnjZuZb4RuWW2voMhCnDxgQKBgQC4TU8XUxs+uVyH+cYjfgMigo7seUtQNaLS1uvsKpPACkUdoTW5T9dXV3/a+M9n7l/UPTBfLc5kcpzfIzUPHsDu4GAHmZMjecxXBxt+PYCoFH4YDgTtAb1SaCrEaOzyR4bsXIMx02sMihcmRKUzEsSSJi2lQAQm2Fp+LBe+a0ZYGQKBgA5xq2yGS3qVLyON2iOp2KOlSPRx97lXFOBp0+/tugrVP/913UEBqD/21GIO/y41xyqas4pjGO7X8QX7Un0NEqBx6PcjgqpagdUJOBL3ClyLX4ZgGhU4ZNNpfQLvbzsy7kfeNqfkoZ+4eogLPpRuL9LDJWvzUJDEHlnT+RFKZHkBAoGBAIJ2d15Tfh3QqSe4MAI/2Yg8U2AiHKYS0cfeS/NJ0H1Ix9RJofYkBpPVZLRlHKy9mShsiClYC5ofZ9ys14p0gq6WNmQq2nVqarPQXC3o64IPv5LXmbUMXjBPfLa8b76x01jNxDEpL8YYmnqEbUzSJEQwsm8f+dBlpulZ3RT2az1BAoGAfM6SqpEHQicmtMZedDYDCKWSWrIApgthApJ6M/DnI2tFuX0Bu8SgIm+PiyhNB4jkvLWaadiHZpXvjL+4BbWaedcDy3Nql1js21oAAuJZ/P14VZyadrxWlCh/GB2jnC3JgXEeeTpvMQgIvQkxF1tyDzlcjmjIw04prU/9nXk6pOQ=";

    //    private static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com";
//    private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String ALIPAY_SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com";
    //    private static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com/gateway.do";
    private static final String ALIPAY_APP_ID = "9021000129622545";
    //    private static final String ALIPAY_APP_ID = ALYConstants.ALIPAY_APP_ID;
    //    private static final String PRIVATE_KEY = ALYConstants.PRIVATE_KEY;
    //private static final String PUBLIC_KEY = ALYConstants.PUBLIC_KEY;
    private static final String RootCertContent = """
            -----BEGIN CERTIFICATE-----
            MIIBszCCAVegAwIBAgIIaeL+wBcKxnswDAYIKoEcz1UBg3UFADAuMQswCQYDVQQG
            EwJDTjEOMAwGA1UECgwFTlJDQUMxDzANBgNVBAMMBlJPT1RDQTAeFw0xMjA3MTQw
            MzExNTlaFw00MjA3MDcwMzExNTlaMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVO
            UkNBQzEPMA0GA1UEAwwGUk9PVENBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE
            MPCca6pmgcchsTf2UnBeL9rtp4nw+itk1Kzrmbnqo05lUwkwlWK+4OIrtFdAqnRT
            V7Q9v1htkv42TsIutzd126NdMFswHwYDVR0jBBgwFoAUTDKxl9kzG8SmBcHG5Yti
            W/CXdlgwDAYDVR0TBAUwAwEB/zALBgNVHQ8EBAMCAQYwHQYDVR0OBBYEFEwysZfZ
            MxvEpgXBxuWLYlvwl3ZYMAwGCCqBHM9VAYN1BQADSAAwRQIgG1bSLeOXp3oB8H7b
            53W+CKOPl2PknmWEq/lMhtn25HkCIQDaHDgWxWFtnCrBjH16/W3Ezn7/U/Vjo5xI
            pDoiVhsLwg==
            -----END CERTIFICATE-----
                
            -----BEGIN CERTIFICATE-----
            MIIF0zCCA7ugAwIBAgIIH8+hjWpIDREwDQYJKoZIhvcNAQELBQAwejELMAkGA1UE
            BhMCQ04xFjAUBgNVBAoMDUFudCBGaW5hbmNpYWwxIDAeBgNVBAsMF0NlcnRpZmlj
            YXRpb24gQXV0aG9yaXR5MTEwLwYDVQQDDChBbnQgRmluYW5jaWFsIENlcnRpZmlj
            YXRpb24gQXV0aG9yaXR5IFIxMB4XDTE4MDMyMTEzNDg0MFoXDTM4MDIyODEzNDg0
            MFowejELMAkGA1UEBhMCQ04xFjAUBgNVBAoMDUFudCBGaW5hbmNpYWwxIDAeBgNV
            BAsMF0NlcnRpZmljYXRpb24gQXV0aG9yaXR5MTEwLwYDVQQDDChBbnQgRmluYW5j
            aWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5IFIxMIICIjANBgkqhkiG9w0BAQEF
            AAOCAg8AMIICCgKCAgEAtytTRcBNuur5h8xuxnlKJetT65cHGemGi8oD+beHFPTk
            rUTlFt9Xn7fAVGo6QSsPb9uGLpUFGEdGmbsQ2q9cV4P89qkH04VzIPwT7AywJdt2
            xAvMs+MgHFJzOYfL1QkdOOVO7NwKxH8IvlQgFabWomWk2Ei9WfUyxFjVO1LVh0Bp
            dRBeWLMkdudx0tl3+21t1apnReFNQ5nfX29xeSxIhesaMHDZFViO/DXDNW2BcTs6
            vSWKyJ4YIIIzStumD8K1xMsoaZBMDxg4itjWFaKRgNuPiIn4kjDY3kC66Sl/6yTl
            YUz8AybbEsICZzssdZh7jcNb1VRfk79lgAprm/Ktl+mgrU1gaMGP1OE25JCbqli1
            Pbw/BpPynyP9+XulE+2mxFwTYhKAwpDIDKuYsFUXuo8t261pCovI1CXFzAQM2w7H
            DtA2nOXSW6q0jGDJ5+WauH+K8ZSvA6x4sFo4u0KNCx0ROTBpLif6GTngqo3sj+98
            SZiMNLFMQoQkjkdN5Q5g9N6CFZPVZ6QpO0JcIc7S1le/g9z5iBKnifrKxy0TQjtG
            PsDwc8ubPnRm/F82RReCoyNyx63indpgFfhN7+KxUIQ9cOwwTvemmor0A+ZQamRe
            9LMuiEfEaWUDK+6O0Gl8lO571uI5onYdN1VIgOmwFbe+D8TcuzVjIZ/zvHrAGUcC
            AwEAAaNdMFswCwYDVR0PBAQDAgEGMAwGA1UdEwQFMAMBAf8wHQYDVR0OBBYEFF90
            tATATwda6uWx2yKjh0GynOEBMB8GA1UdIwQYMBaAFF90tATATwda6uWx2yKjh0Gy
            nOEBMA0GCSqGSIb3DQEBCwUAA4ICAQCVYaOtqOLIpsrEikE5lb+UARNSFJg6tpkf
            tJ2U8QF/DejemEHx5IClQu6ajxjtu0Aie4/3UnIXop8nH/Q57l+Wyt9T7N2WPiNq
            JSlYKYbJpPF8LXbuKYG3BTFTdOVFIeRe2NUyYh/xs6bXGr4WKTXb3qBmzR02FSy3
            IODQw5Q6zpXj8prYqFHYsOvGCEc1CwJaSaYwRhTkFedJUxiyhyB5GQwoFfExCVHW
            05ZFCAVYFldCJvUzfzrWubN6wX0DD2dwultgmldOn/W/n8at52mpPNvIdbZb2F41
            T0YZeoWnCJrYXjq/32oc1cmifIHqySnyMnavi75DxPCdZsCOpSAT4j4lAQRGsfgI
            kkLPGQieMfNNkMCKh7qjwdXAVtdqhf0RVtFILH3OyEodlk1HYXqX5iE5wlaKzDop
            PKwf2Q3BErq1xChYGGVS+dEvyXc/2nIBlt7uLWKp4XFjqekKbaGaLJdjYP5b2s7N
            1dM0MXQ/f8XoXKBkJNzEiM3hfsU6DOREgMc1DIsFKxfuMwX3EkVQM1If8ghb6x5Y
            jXayv+NLbidOSzk4vl5QwngO/JYFMkoc6i9LNwEaEtR9PhnrdubxmrtM+RjfBm02
            77q3dSWFESFQ4QxYWew4pHE0DpWbWy/iMIKQ6UZ5RLvB8GEcgt8ON7BBJeMc+Dyi
            kT9qhqn+lw==
            -----END CERTIFICATE-----
                
            -----BEGIN CERTIFICATE-----
            MIICiDCCAgygAwIBAgIIQX76UsB/30owDAYIKoZIzj0EAwMFADB6MQswCQYDVQQG
            EwJDTjEWMBQGA1UECgwNQW50IEZpbmFuY2lhbDEgMB4GA1UECwwXQ2VydGlmaWNh
            dGlvbiBBdXRob3JpdHkxMTAvBgNVBAMMKEFudCBGaW5hbmNpYWwgQ2VydGlmaWNh
            dGlvbiBBdXRob3JpdHkgRTEwHhcNMTkwNDI4MTYyMDQ0WhcNNDkwNDIwMTYyMDQ0
            WjB6MQswCQYDVQQGEwJDTjEWMBQGA1UECgwNQW50IEZpbmFuY2lhbDEgMB4GA1UE
            CwwXQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxMTAvBgNVBAMMKEFudCBGaW5hbmNp
            YWwgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkgRTEwdjAQBgcqhkjOPQIBBgUrgQQA
            IgNiAASCCRa94QI0vR5Up9Yr9HEupz6hSoyjySYqo7v837KnmjveUIUNiuC9pWAU
            WP3jwLX3HkzeiNdeg22a0IZPoSUCpasufiLAnfXh6NInLiWBrjLJXDSGaY7vaokt
            rpZvAdmjXTBbMAsGA1UdDwQEAwIBBjAMBgNVHRMEBTADAQH/MB0GA1UdDgQWBBRZ
            4ZTgDpksHL2qcpkFkxD2zVd16TAfBgNVHSMEGDAWgBRZ4ZTgDpksHL2qcpkFkxD2
            zVd16TAMBggqhkjOPQQDAwUAA2gAMGUCMQD4IoqT2hTUn0jt7oXLdMJ8q4vLp6sg
            wHfPiOr9gxreb+e6Oidwd2LDnC4OUqCWiF8CMAzwKs4SnDJYcMLf2vpkbuVE4dTH
            Rglz+HGcTLWsFs4KxLsq7MuU+vJTBUeDJeDjdA==
            -----END CERTIFICATE-----
                
            -----BEGIN CERTIFICATE-----
            MIIDxTCCAq2gAwIBAgIUEMdk6dVgOEIS2cCP0Q43P90Ps5YwDQYJKoZIhvcNAQEF
            BQAwajELMAkGA1UEBhMCQ04xEzARBgNVBAoMCmlUcnVzQ2hpbmExHDAaBgNVBAsM
            E0NoaW5hIFRydXN0IE5ldHdvcmsxKDAmBgNVBAMMH2lUcnVzQ2hpbmEgQ2xhc3Mg
            MiBSb290IENBIC0gRzMwHhcNMTMwNDE4MDkzNjU2WhcNMzMwNDE4MDkzNjU2WjBq
            MQswCQYDVQQGEwJDTjETMBEGA1UECgwKaVRydXNDaGluYTEcMBoGA1UECwwTQ2hp
            bmEgVHJ1c3QgTmV0d29yazEoMCYGA1UEAwwfaVRydXNDaGluYSBDbGFzcyAyIFJv
            b3QgQ0EgLSBHMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAOPPShpV
            nJbMqqCw6Bz1kehnoPst9pkr0V9idOwU2oyS47/HjJXk9Rd5a9xfwkPO88trUpz5
            4GmmwspDXjVFu9L0eFaRuH3KMha1Ak01citbF7cQLJlS7XI+tpkTGHEY5pt3EsQg
            wykfZl/A1jrnSkspMS997r2Gim54cwz+mTMgDRhZsKK/lbOeBPpWtcFizjXYCqhw
            WktvQfZBYi6o4sHCshnOswi4yV1p+LuFcQ2ciYdWvULh1eZhLxHbGXyznYHi0dGN
            z+I9H8aXxqAQfHVhbdHNzi77hCxFjOy+hHrGsyzjrd2swVQ2iUWP8BfEQqGLqM1g
            KgWKYfcTGdbPB1MCAwEAAaNjMGEwHQYDVR0OBBYEFG/oAMxTVe7y0+408CTAK8hA
            uTyRMB8GA1UdIwQYMBaAFG/oAMxTVe7y0+408CTAK8hAuTyRMA8GA1UdEwEB/wQF
            MAMBAf8wDgYDVR0PAQH/BAQDAgEGMA0GCSqGSIb3DQEBBQUAA4IBAQBLnUTfW7hp
            emMbuUGCk7RBswzOT83bDM6824EkUnf+X0iKS95SUNGeeSWK2o/3ALJo5hi7GZr3
            U8eLaWAcYizfO99UXMRBPw5PRR+gXGEronGUugLpxsjuynoLQu8GQAeysSXKbN1I
            UugDo9u8igJORYA+5ms0s5sCUySqbQ2R5z/GoceyI9LdxIVa1RjVX8pYOj8JFwtn
            DJN3ftSFvNMYwRuILKuqUYSHc2GPYiHVflDh5nDymCMOQFcFG3WsEuB+EYQPFgIU
            1DHmdZcz7Llx8UOZXX2JupWCYzK1XhJb+r4hK5ncf/w8qGtYlmyJpxk3hr1TfUJX
            Yf4Zr0fJsGuv
            -----END CERTIFICATE-----""";
    private static final String AppCertContent = """
            -----BEGIN CERTIFICATE-----
            MIIDmTCCAoGgAwIBAgIQICMQCJeH6nsm/7ghouuvyjANBgkqhkiG9w0BAQsFADCBkTELMAkGA1UE
            BhMCQ04xGzAZBgNVBAoMEkFudCBGaW5hbmNpYWwgdGVzdDElMCMGA1UECwwcQ2VydGlmaWNhdGlv
            biBBdXRob3JpdHkgdGVzdDE+MDwGA1UEAww1QW50IEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1
            dGhvcml0eSBDbGFzcyAyIFIxIHRlc3QwHhcNMjMxMDA4MDU1ODI3WhcNMjQxMDEyMDU1ODI3WjBr
            MQswCQYDVQQGEwJDTjEfMB0GA1UECgwWaG94YnFlMjg2MEBzYW5kYm94LmNvbTEPMA0GA1UECwwG
            QWxpcGF5MSowKAYDVQQDDCEyMDg4NzIxMDE2NDI4MTY4LTkwMjEwMDAxMjk2MjI1NDUwggEiMA0G
            CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCFyNU72HF65+UA6TX/6W7kvVrgWZSYT8QZCWZ48KTQ
            La0UeY9XCc+uq6rfLa9oOWqOaV5h13PQg8woEIxqKDGwhqzjFdF9pvCqiwAsFrGGprm05K7uibpu
            gdaYM9cYm7uM2qZ7T82om/wjdCO5Rw2AU5wSrZv4K9KrgvIbJaD8kbQePM25HSSQ5hDvawCDSijp
            mqEULBvH6zQKSTloP9EkTPiKvRU+511bJjAX+JA5ec+q+NCjo4yhdSIVBVN/kVYYTWq20Fptkqa1
            SjM+v73zBjilXvxe+sORfLN+/VPcQlBz8W0GAm5a59IaVW8Tm2dwBN0EPUYWveXGDmHMUe2ZAgMB
            AAGjEjAQMA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQsFAAOCAQEAHriMcRGRBlrZMMA00kN5
            XVgU2OsaS7O3f0IENrDAnrgmxj0yLyrJA5Z7Od/GNLekcMZ9FduIEhgfwoYQztop7G2j16lfWNJd
            3D3NlTwhUJW/v0g84KEMWGDbKFINPg7w0TTYxRYqUsKsqP6o6U6p3X9cuTMBcLMLrLitjmdsoeSl
            ExuWAbvqYF24pwYBG7eyFfHiSevMQ6scCXuEmlbjilbRtsS8TFmeuTbYQ6zmbB8G0YDFv+6sx8DW
            MLMi9m+jw2l4OXr+r8FFy3L9ibWSVKoD8AtLPagZFtCuiTqCNv8cwk4QoSCmF4VtlVIYkUZ88WSK
            DXHbo8Nc97gtrfkAZw==
            -----END CERTIFICATE-----""";
    private static final String AlipayPublicCertContent = """
            -----BEGIN CERTIFICATE-----
            MIIDszCCApugAwIBAgIQICMQEuJZ8ufws0PwJqMVyDANBgkqhkiG9w0BAQsFADCBkTELMAkGA1UE
            BhMCQ04xGzAZBgNVBAoMEkFudCBGaW5hbmNpYWwgdGVzdDElMCMGA1UECwwcQ2VydGlmaWNhdGlv
            biBBdXRob3JpdHkgdGVzdDE+MDwGA1UEAww1QW50IEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1
            dGhvcml0eSBDbGFzcyAyIFIxIHRlc3QwHhcNMjMxMDEyMDg1MTAzWhcNMjQxMDExMDg1MTAzWjCB
            hDELMAkGA1UEBhMCQ04xHzAdBgNVBAoMFmhveGJxZTI4NjBAc2FuZGJveC5jb20xDzANBgNVBAsM
            BkFsaXBheTFDMEEGA1UEAww65pSv5LuY5a6dKOS4reWbvSnnvZHnu5zmioDmnK/mnInpmZDlhazl
            j7gtMjA4ODcyMTAxNjQyODE2ODCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIHwb8DF
            /EMKxUFJrZH5w7BHzaiJOjT4uu1VoV0Zt17WENIfNkpUiV8pXy9ksTCV0ZNrN8GG3mSVHTzW6bgU
            y3XwVfDVD190EPPNjl6wnxiEWcR14O9F6vh59yRx6AN5Xcgu6wgMjwJsnBgKZkG4/YEd5PY/KSol
            BcrfFwGZacByTq/ylpnk4LSHxU3HbWV8dERErdn1ZPZqJO1tCVwXQwtM5lTuvdlfLAhF9tRd8/9m
            0roxCLbagyAjKx1HuHD3LecR3c/ej/xzS1J8rPkGbAddRqAR3RA6qnuzPYOkhyCf7Zps+Tnf5/Qg
            U699iBEKVrPk632IEmWoMitrQr3upsECAwEAAaMSMBAwDgYDVR0PAQH/BAQDAgTwMA0GCSqGSIb3
            DQEBCwUAA4IBAQB1mawNpWLeAssDIdSyHdHZnP8ysnN7Ib4TdvKgXxrcQeZhZrzp2QQ3bYNDD0h4
            ttAOzBpaBgeUefZN2cyHYDkdlKbGDWEwdS3iIwdfOtq0V2hiGkLd2rpl3XBnfwUHj2ZbE4MNzS5m
            MFqrvTigCcPixRcvciEvyS7FR3IMfyAtktsN4fPuXUbcCt6KMfO3HZzuTaaLDCQxgJ/pv1gdsUHe
            OJ2Z5yw7mV5ZE7iTG3yrfLepDIgGtkrenQsHlR/+CUW7Uvvel1nf4GAmVEnBu9Id6YpdN7suNmts
            hxvkSmWTsQR7d4ZBcUrGMtDB/CywPy0xBKXLz4lC35t7kvljZfo5
            -----END CERTIFICATE-----
            -----BEGIN CERTIFICATE-----
            MIIDszCCApugAwIBAgIQIBkIGbgVxq210KxLJ+YA/TANBgkqhkiG9w0BAQsFADCBhDELMAkGA1UE
            BhMCQ04xFjAUBgNVBAoMDUFudCBGaW5hbmNpYWwxJTAjBgNVBAsMHENlcnRpZmljYXRpb24gQXV0
            aG9yaXR5IHRlc3QxNjA0BgNVBAMMLUFudCBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBBdXRob3Jp
            dHkgUjEgdGVzdDAeFw0xOTA4MTkxMTE2MDBaFw0yNDA4MDExMTE2MDBaMIGRMQswCQYDVQQGEwJD
            TjEbMBkGA1UECgwSQW50IEZpbmFuY2lhbCB0ZXN0MSUwIwYDVQQLDBxDZXJ0aWZpY2F0aW9uIEF1
            dGhvcml0eSB0ZXN0MT4wPAYDVQQDDDVBbnQgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9y
            aXR5IENsYXNzIDIgUjEgdGVzdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMh4FKYO
            ZyRQHD6eFbPKZeSAnrfjfU7xmS9Yoozuu+iuqZlb6Z0SPLUqqTZAFZejOcmr07ln/pwZxluqplxC
            5+B48End4nclDMlT5HPrDr3W0frs6Xsa2ZNcyil/iKNB5MbGll8LRAxntsKvZZj6vUTMb705gYgm
            VUMILwi/ZxKTQqBtkT/kQQ5y6nOZsj7XI5rYdz6qqOROrpvS/d7iypdHOMIM9Iz9DlL1mrCykbBi
            t25y+gTeXmuisHUwqaRpwtCGK4BayCqxRGbNipe6W73EK9lBrrzNtTr9NaysesT/v+l25JHCL9tG
            wpNr1oWFzk4IHVOg0ORiQ6SUgxZUTYcCAwEAAaMSMBAwDgYDVR0PAQH/BAQDAgTwMA0GCSqGSIb3
            DQEBCwUAA4IBAQBWThEoIaQoBX2YeRY/I8gu6TYnFXtyuCljANnXnM38ft+ikhE5mMNgKmJYLHvT
            yWWWgwHoSAWEuml7EGbE/2AK2h3k0MdfiWLzdmpPCRG/RJHk6UB1pMHPilI+c0MVu16OPpKbg5Vf
            LTv7dsAB40AzKsvyYw88/Ezi1osTXo6QQwda7uefvudirtb8FcQM9R66cJxl3kt1FXbpYwheIm/p
            j1mq64swCoIYu4NrsUYtn6CV542DTQMI5QdXkn+PzUUly8F6kDp+KpMNd0avfWNL5+O++z+F5Szy
            1CPta1D7EQ/eYmMP+mOQ35oifWIoFCpN6qQVBS/Hob1J/UUyg7BW
            -----END CERTIFICATE-----""";
    private static final ApiClient apiClient;

    static {
        apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(ALIPAY_SERVER_URL);
        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置应用ID
        alipayConfig.setAppId(ALIPAY_APP_ID);
        // 设置应用私钥
        alipayConfig.setPrivateKey(PRIVATE_KEY);
        // 公钥模式
        // 设置支付宝公钥
        // alipayConfig.setAlipayPublicKey(PUBLIC_KEY);
        String pathPrefix = "D:/forSoftware/ali/";
        // 证书模式
        // 支付宝公钥证书
//        alipayConfig.setAlipayPublicCertContent(AlipayPublicCertContent);
        // appPublicCert.crt alipayPublicCert.crt alipayRootCert.crt
        alipayConfig.setAlipayPublicCertPath(pathPrefix + "alipayPublicCert.crt");
        // 应用公钥证书
        alipayConfig.setAppCertPath(pathPrefix + "appPublicCert.crt");
        // alipayConfig.setAppCertContent(AppCertContent);
        // 支付宝根证书
        alipayConfig.setRootCertPath(pathPrefix + "alipayRootCert.crt");
        // alipayConfig.setRootCertContent(RootCertContent);
//        alipayConfig.setEncryptKey(PUBLIC_KEY);
//        alipayConfig.setEncryptType("RSA");

        try {
            apiClient.setAlipayConfig(alipayConfig);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 余额查询
     * {@code {
     * availableAmount: 999999.89
     * extCardInfo: null
     * freezeAmount: 0.00
     * }}
     */
    @SneakyThrows
    @Test
    public void query() {
        AlipayFundAccountApi api = new AlipayFundAccountApi();
        String merchantUserId = null;
        String alipayUserId = "2088721016428168";
//        String alipayUserId = "2021003180613176";
//        String alipayUserId = "2021002133634470";
//        String alipayUserId = "2088041734690435";
        String alipayOpenId = null;
        String accountProductCode = null;
        String accountType = "ACCTRANS_ACCOUNT";
        String accountSceneCode = null;
        String extInfo = null;
        try {
            AlipayFundAccountQueryResponseModel response = api.query(merchantUserId, alipayUserId, alipayOpenId, accountProductCode, accountType, accountSceneCode, extInfo);
            log.info("{}", response);
        } catch (ApiException e) {
            log.error("", e);
            AlipayFundAccountQueryDefaultResponse errorObject = (AlipayFundAccountQueryDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
    }

    /**
     * 支付宝提现/转账
     * out_biz_nobody	String	必选	64
     * 商家侧唯一订单号，由商家自定义。对于不同转账请求，商家需保证该订单号在自身系统唯一。
     * 201806300001
     * trans_amountbody	Price	必选	20
     * 订单总金额，单位为元，不支持千位分隔符，精确到小数点后两位，取值范围[0.1,100000000]。
     * 23.00
     * biz_scenebody	String	必选	64
     * 业务场景。单笔无密转账固定为 DIRECT_TRANSFER。
     * DIRECT_TRANSFER
     * product_codebody	String	必选	64
     * 销售产品码。单笔无密转账固定为 TRANS_ACCOUNT_NO_PWD。
     * TRANS_ACCOUNT_NO_PWD
     * order_titlebody	String	必选	128
     * 转账业务的标题，用于在支付宝用户的账单里显示。
     * 201905代发
     * payee_infobody	Participant	必选
     * 收款方信息
     */
    @Test
    public void transfer() {

        AlipayFundTransUniApi api = new AlipayFundTransUniApi(apiClient);
        AlipayFundTransUniTransferModel data = new AlipayFundTransUniTransferModel();
        String orderId = IdUtils.fastSimpleUUID();
        log.info("{}", orderId);
        data.setOutBizNo(orderId);
        data.setRemark("备注");
        data.setBusinessParams("{\"payer_show_name_use_alias\":\"true\"}");
        data.setBizScene("DIRECT_TRANSFER");

        data.setTransAmount("0.1");
        data.setProductCode("TRANS_ACCOUNT_NO_PWD");
        data.setOrderTitle("201905代发");

        Participant payeeInfo = new Participant();
        payeeInfo.setIdentity("15868372831");
        payeeInfo.setIdentityType("ALIPAY_LOGON_ID");
        // 当 identity_type=ALIPAY_LOGON_ID 时，本字段必填
        payeeInfo.setName("李宁");/* Participant payeeInfo = new Participant();
        payeeInfo.setIdentity("2088722016338318");
        payeeInfo.setIdentityType("ALIPAY_USER_ID");
        // 当 identity_type=ALIPAY_LOGON_ID 时，本字段必填
        payeeInfo.setName("mbnkph3072");*/
        data.setPayeeInfo(payeeInfo);
        try {
            AlipayFundTransUniTransferResponseModel response = api.transfer(data);
            log.info("{}", response);
        } catch (ApiException e) {
            log.error("", e);

            AlipayFundTransUniTransferDefaultResponse errorObject = (AlipayFundTransUniTransferDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
    }

    /**
     * {@code {
     * orderId: 20231012110070000002160000448625
     * outBizNo: 7a770df6c20845bbbbbd5535ad12ad80
     * payFundOrderId: 20231012110070001502160000451602
     * settleSerialNo: null
     * status: SUCCESS
     * transDate: 2023-10-12 17:42:25
     * }}
     */
    @Test
    public void transferDev() {
        AlipayFundTransUniApi api = new AlipayFundTransUniApi(apiClient);
        AlipayFundTransUniTransferModel data = new AlipayFundTransUniTransferModel();
        String orderId = IdUtils.fastSimpleUUID();
        log.info("{}", orderId);
        data.setOutBizNo(orderId);
        data.setRemark("备注");
        data.setBusinessParams("{\"payer_show_name_use_alias\":\"true\"}");
        data.setBizScene("DIRECT_TRANSFER");

        data.setTransAmount("0.1");
        data.setProductCode("TRANS_ACCOUNT_NO_PWD");
        data.setOrderTitle("test-0.1");

        Participant payeeInfo = new Participant();
        payeeInfo.setIdentity("mbnkph3072@sandbox.com");
        payeeInfo.setIdentityType("ALIPAY_LOGON_ID");
        // 当 identity_type=ALIPAY_LOGON_ID 时，本字段必填
        payeeInfo.setName("mbnkph3072");/* Participant payeeInfo = new Participant();
        payeeInfo.setIdentity("2088722016338318");
        payeeInfo.setIdentityType("ALIPAY_USER_ID");
        // 当 identity_type=ALIPAY_LOGON_ID 时，本字段必填
        payeeInfo.setName("mbnkph3072");*/
        data.setPayeeInfo(payeeInfo);
        try {
            AlipayFundTransUniTransferResponseModel response = api.transfer(data);
            log.info("{}", response);
        } catch (ApiException e) {
            log.error("", e);

            AlipayFundTransUniTransferDefaultResponse errorObject = (AlipayFundTransUniTransferDefaultResponse) e.getErrorObject();
            System.out.println("调用失败:" + errorObject);
        }
    }
}
