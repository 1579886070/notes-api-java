
package com.zxxwl.common.interceptor;

import com.zxxwl.common.annotation.SafetyTokenCheck;
import com.zxxwl.common.constants.SysConstants;
import com.zxxwl.common.crypto.Hash;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * SafetyTokenCheckInterceptor
 *
 * @author zhouxin
 * @author qingyu 2023.02.28
 * @apiNote 拦截器中验证Http头中的Token是否正确
 * @since 2020/10/29 10:40
 */


@Slf4j
@Component
public class SafetyTokenCheckInterceptor implements AsyncHandlerInterceptor {

    /**
     * 两小时以内访问
     */
    private static final long SAFETY_TOKEN_TIME_OUT = 1000 * 60 * 60 * 2;
    /**
     * 时间误差 单位 毫秒ms 1000*60 暂定1分钟
     */

    private static final long SAFETY_TOKEN_TIME_ERROR = 1000 * 60;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {

            Method method = handlerMethod.getMethod();
            SafetyTokenCheck safetyTokenCheck = method.getAnnotation(SafetyTokenCheck.class);

            if (safetyTokenCheck != null) {
                if (!effectiveValidator(SysHttpRequestUtil.getSafetyToken(request), SysHttpRequestUtil.getSafetyCode(request))) {
                    log.warn("【拦截器，safetyTokenCheck校验失败，非法请求");
                    send(response, HttpStatus.BAD_REQUEST.value(), "非法请求！");
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /**
     * 验证防篡改token是否是合法的
     * FIXME 若以后存在时区概念 需进行时区转换
     *
     * @param safety 安全token
     * @param time   加密值（日期时间戳（精确到毫秒））
     * @return R
     */

    private static boolean effectiveValidator(String safety, String time) {
        try {
            if (StringUtils.isBlank(safety) || StringUtils.isBlank(time)) {
                return false;
            } else {
                // 当前时间 - 请求时间 (一般请求时间早于当前系统时间,除非存在时差)
                long timeDifference = System.currentTimeMillis() - Long.parseLong(time);

                // 是否在两小时以内访问
                if (timeDifference > -SAFETY_TOKEN_TIME_ERROR && timeDifference < SAFETY_TOKEN_TIME_OUT) {
                    return judgeEncrypt(safety, time);
                } else {
                    log.warn("【拦截器，时间校验失败，无权访问！】");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("【验证token是否是合法方法失败】", e);
            return false;
        }
    }

    /**
     * 验证
     * <p>
     * 加密规则
     * SAFETY_VAL + SAFETY_SECRET + 时间戳 组合，进行SHA1哈希计算
     *
     * @param safety 安全token
     * @param keyVal 加密值
     * @return R
     */

    public static Boolean judgeEncrypt(String safety, String keyVal) {
        String sign = Hash.build(SysConstants.SAFETY_VAL + SysConstants.SAFETY_SECRET + keyVal, "SHA-1");

        return sign.equals(safety.toLowerCase());
    }

    public void send(HttpServletResponse response, int code, String message) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String info = URLEncoder.encode(message, StandardCharsets.UTF_8);

        // TODO 同步
        response.setStatus(code);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-api-message,x-api-total,x-api-page,x-api-size,x-api-pages,x-author-by,x-api-version");
        response.setHeader("Access-Control-Expose-Headers", "x-api-message,x-api-total,x-api-page,x-api-size,x-api-pages,x-author-by,x-api-version");
        response.setHeader("x-api-message", Base64.getEncoder().encodeToString(info != null ? info.getBytes() : new byte[0]));
        response.setHeader("x-author-by", SysConstants.API_AUTHOR_BY);
        response.setHeader("x-api-version", SysConstants.API_VERSION);
    }

}
