package com.zxxwl.common.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author zhouxin
 * @since 2020/11/19 15:56
 */
public class JwtUtil {
    /**
     * token秘钥
     */
    public static final String SECRET = "N70U3NwvuUc8gSgH+qEbBy2i6a0=";
    /**
     * token 过期时间: 30天
     */
    public static final int calendarField = Calendar.DATE;
    public static final int calendarInterval = 60;

    /**
     * 创建token
     * <p>
     * JWT构成: header, payload, signature
     * </p>
     *
     * @param memberId 用户id
     * @param phone    用户手机号
     * @return R
     */
    public static String create(String memberId, String phone) {
        Date iatDate = new Date();

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(calendarField, calendarInterval);
        Date expiresDate = nowTime.getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        String token = null;
        token = JWT.create().withHeader(map)
                .withClaim("memberId", memberId)
                .withClaim("phone", phone)
                .withIssuedAt(iatDate)
                .withExpiresAt(expiresDate)
                .sign(Algorithm.HMAC256(SECRET));

        return token;
    }

    /**
     * 解密token
     *
     * @param token
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
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            return "";
        }

        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("memberId").asString();
    }

}
