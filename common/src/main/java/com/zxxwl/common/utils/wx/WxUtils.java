package com.zxxwl.common.utils.wx;

import com.zxxwl.common.constants.WxConstants;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;

/**
 * @author zhouxin
 * @author qingyu
 * @since 2022/3/10 16:44
 */
@Slf4j
public class WxUtils {

    public static String getSignature(String sign) {
        String signature = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(sign.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();

        return result;

    }

    public static String decrypt(String appId, String encryptedData, String sessionKey, String iv) {
        // TODO
        /*String result = "";
        try {
            AES aes = new AES();
            byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                result = new String(WxPKCS7Encoder.decode(resultByte));
                JSONObject jsonObject = JSONObject.parseObject(result);
                String decryptAppId = jsonObject.getJSONObject("watermark").getString("appid");
                if (!appId.equals(decryptAppId)) {
                    result = "";
                }
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }

        log.info("【小程序授权，返回的数据：[{}]】", result);

        return result;*/
        return null;
    }

    /**
     * 微信payTime时间 转换
     * yyyyMMddHHmmss
     * 订单支付时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2">查单</a>
     *
     * @param stringTime timeString
     * @return payTime
     */
    public static LocalDateTime wxTimeStringParse(String stringTime) {
        LocalDateTime payTime = null;
        try {
            payTime = LocalDateTime.parse(stringTime, DateTimeFormatter.ofPattern(WxConstants.WX_PAY_TIME_FORMAT));
        } catch (Exception e) {
            payTime = LocalDateTime.now();
        }
        return payTime;
    }
}
