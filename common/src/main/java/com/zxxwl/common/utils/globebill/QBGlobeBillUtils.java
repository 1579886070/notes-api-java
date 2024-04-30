package com.zxxwl.common.utils.globebill;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 重庆市钱宝科技服务有限公司
 * <a href="https://www.globebill.com">重庆市钱宝科技服务有限公司</a>
 * <a href="https://ppopen.globebill.com/">OpenApi doc|接口文档</a>
 * <p>
 * {@code {
 * "accessId":4064,
 * "sn":"GB10006691",
 * "merchantId":128255,
 * "terminalId":12576350,
 * "merchantPrivateKey":"",
 * "merchantPublicKey":"",
 * "qbPublicKey":""
 * }}
 * </p>
 *
 * @author qingyu
 */
@Slf4j
public class QBGlobeBillUtils {
    /**
     * 接入商编号
     */
    public static String ACCESS_ID = "";
    /**
     * 设备TUSN号，有固定TUSN号可使用sn参数交易
     * FIXME 从配置文件或数据库（缓存|持久化存储）冲获取 不要放代码里
     */
    public static final String SN = "";

    /**
     * 私钥，开发者妥善保存
     * FIXME 从配置文件或数据库（缓存|持久化存储）冲获取 不要放代码里
     */
    public static String MERCHANT_PRIVATE_KEY = "";
    /**
     * 平台公钥-来钱乐公钥（平台公钥， 用途：提供给接入商接收数据后验签）
     * FIXME 从配置文件或数据库（缓存|持久化存储）冲获取 不要放代码里
     */
    public static String GLOBEBILL_PUBLIC_KEY = "";
    /**
     * 接入商公钥（用途：提供给来钱乐进行接收数据后验签）
     * FIXME 从配置文件或数据库（缓存|持久化存储）冲获取 不要放代码里
     */
    public static String MERCHANT_PUBLIC_KEY = "";

    /**
     * 加密方式|签名方式
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 服务器
     */
    public static String SERVER = "https://ppapi.globebill.com/";

    /**
     * 交易查询
     * <a href="https://ppopen.globebill.com/qrcode/query">交易查询</a>
     */
    public static final String PATH_QUERY = "/qrcode/query";
    /**
     * 统一支付
     * <a href="https://ppopen.globebill.com/qrcode/pay">统一支付</a>
     */
    public static final String PATH_PAY = "/qrcode/pay";
    /**
     * 退款
     * <a href="https://ppopen.globebill.com/qrcode/refund">退款</a>
     */
    public static final String PATH_REFUND = "/qrcode/refund";
    /**
     * 10031: 微信扫码支付
     * 10032: 微信公众号支付
     * 10033: 微信刷卡支付
     * 10036: 微信小程序
     * 10041: 支付宝扫码支付
     * 10042: 支付宝刷卡支付
     * 10043: 支付窗支付
     * 10101: 银联二维码扫码支付
     * 10102: 银联二维码被扫
     * 10103: 银联云闪付(ApplePay)
     * 10104: 银联行业码
     * 10341: 数字人民币主扫
     * 10342: 数字人民币被扫
     * 10343: 数字人民币在线支付
     */
    public static final int PAY_MODE_ID_WX_MINI = 10036;
    /**
     * 日期|时间戳格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String ACCESS_ID_TEST = "3011";

    // 服务器 测试环境
    public static String SERVER_TEST = "http://119.23.124.194:60002";


    public static String DEV_PRIVATE_KEY_TEST = "";
    // 对应的公钥，提供给钱宝
    // public static String DevPublicKey = "";

    // 平台公钥，由钱宝提供
    public static String QB_PUBLIC_KEY_TEST = "M";

    public static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(QBGlobeBillUtils.DATE_FORMAT));
    }

    public static void logMsg(Exception e) {
        e.printStackTrace();
    }

    public static void logMsg(String msg) {
        System.out.println(msg);
    }

    public static PrivateKey getPrivateKey(String priKey) {
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static PublicKey getPublicKey(String pubKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec PubKeySpec = new X509EncodedKeySpec(
                    Base64.decode(pubKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            publicKey = keyFactory.generatePublic(PubKeySpec);
        } catch (Exception e1) {
        }
        return publicKey;
    }

    //SHA256withRSA签名
    public static String sign256(String data, String privateKey) {
        byte[] sigByte = QBGlobeBillUtils.sign256(data.getBytes(StandardCharsets.UTF_8), getPrivateKey(privateKey));
        String signVal = Base64.toBase64String(sigByte);
        return signVal;
    }

    //SHA256withRSA签名
    public static byte[] sign256(byte[] data, PrivateKey privateKey) {
        byte[] signed = null;
        Signature signature = null;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            signed = signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signed;
    }

    //SHA256withRSA验签
    public static boolean verify256(String data, String sign, String pubKey) {
        if (data == null || sign == null) {
            return false;
        }
        try {
            byte[] signBytes = Base64.decode(sign);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
            signetcheck.initVerify(getPublicKey(pubKey));
            signetcheck.update(dataBytes);
            return signetcheck.verify(signBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SHA256withRSA验签
     *
     * @param data   body
     * @param sign   signValue headers
     * @param pubKey pubKey
     * @return success
     */
    public static boolean verify256(ByteBuffer data, String sign, String pubKey) {
        if (data == null || sign == null) {
            return false;
        }
        try {
            byte[] signBytes = Base64.decode(sign);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(getPublicKey(pubKey));
            signature.update(data);
            return signature.verify(signBytes);
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    public static String readStream(InputStream stream) {
        String str = null;
        try {
            byte[] buffer = new byte[64];
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int read;
            while ((read = stream.read(buffer)) > 0) {
                bo.write(buffer, 0, read);
            }
            str = bo.toString(StandardCharsets.UTF_8);
        } catch (Exception ignore) {
        }
        return str;
    }


    public static String httpPost(String url, String data, Map<String, String> headers, Map<String, String> respHeaders) {
        return QBGlobeBillUtils.httpPost(url, data, headers, respHeaders, StandardCharsets.UTF_8, true);
    }

    public static String httpPost(String url, String data, Map<String, String> headers, Map<String, String> respHeaders, Charset charset, boolean logInfo) {
        if (logInfo) {
            logMsg(String.format("请求：%s\r\n%s\n", url, data));
        }
        try {
            var uri = new URL(url);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("POST");
            if (headers != null && headers.size() > 0) {
                try {
                    Iterator<String> iter = headers.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String val = headers.get(key);
                        if (key != null && key.trim().length() > 0 && val != null) {
                            connection.setRequestProperty(key, val);
                            if (logInfo) {
                                logMsg(String.format("%s : %s", key, val));
                            }
                        }
                    }
                    if (headers.size() > 1) {
                        logMsg(headers.toString());
                    }
                } catch (Exception ignore) {
                }
            }
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(60 * 1000);
            connection.setConnectTimeout(10 * 1000);

            var out = connection.getOutputStream();
            out.write(data.getBytes(charset));
            out.flush();
            out.close();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                try {
                    respHeaders.put(key, map.get(key).get(0));
                } catch (Exception ignore) {
                }
            }
            var res = readStream(connection.getInputStream());
            if (logInfo) {
                logMsg(String.format("响应码： %s， 响应：%s， %s\n\n", connection.getResponseCode(), res, connection.getResponseMessage()));
            }
            return res;
        } catch (Exception e) {
            logMsg(e);
            return null;
        }
    }

}
