package com.zxxwl.common.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JWT工具类
 *
 * @author zhouxin 2020/11/19 15:56
 * @author qingyu 2023.09.09
 * @since 2020/11/19 15:56
 */
public class JwtUtilV2 {
    /**
     * token秘钥
     */
    public static final String SECRET = "N70U3NwvuUc8gSgH+qEbBy2i9999";
    /**
     * token 过期时间: 30天
     */
//    public static final int calendarField = Calendar.DATE;
    public static final TemporalUnit calendarField = java.time.temporal.ChronoUnit.DAYS;
    public static final int calendarInterval = 60;

    /**
     * 创建token
     * <p>
     * JWT构成: header, payload, signature
     * </p>
     *
     * @param memberId 用户id
     * @return R
     */
    public static String create(String memberId) {

        Instant nowTime = Instant.now();
        Instant expiresTime = nowTime.plus(calendarInterval, calendarField);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        return JWT.create().withHeader(map)
                .withClaim("memberId", memberId)
                .withIssuedAt(nowTime)
                .withExpiresAt(expiresTime)
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 校验 token
     *
     * @param token token
     * @return R
     */
    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            verifier.verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 获取用户id
     *
     * @param token JWT
     * @return R
     */
    public static String getMemberId(String token) {
        AtomicReference<String> memberId = new AtomicReference<>("");
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token);
            memberId.set(jwt.getClaim("memberId").asString());
        } catch (Exception e) {
            memberId.set("");
        }
        return memberId.get();
    }
    public static String createTemp(Map<String,Object> data) {

        Instant nowTime = Instant.now();
        Instant expiresTime = nowTime.plus(30, ChronoUnit.MINUTES);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        return JWT.create().withHeader(map)
                .withClaim("data", data)
                .withIssuedAt(nowTime)
                .withExpiresAt(expiresTime)
                .sign(Algorithm.HMAC256(SECRET));
    }
    public static Map<String,Object> getTempOpenid(String token) {
        AtomicReference<Map<String,Object>> openid = new AtomicReference<>();
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token);
            openid.set(jwt.getClaim("data").asMap());
        } catch (Exception e) {
            openid.set(Map.of());
        }
        return openid.get();
    }

    public static String getShopId(String headerToken) {
        // TODO 解析，是否存到JWT共享同一个token, 还是另外获取一个shop-token进行解耦

        return "1702235541994008578";
    }
}
