
package com.zxxwl.common.aspect;

import com.zxxwl.common.annotation.ApiReqLimit;
import com.zxxwl.common.api.redis.RedisService;
import com.zxxwl.common.crypto.CryptoUtil;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.zxxwl.exception.CustomCallException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;


/**
 * 接口 请求限制
 *
 * @author qingyu 2023.05.12
 * @apiNote 基于 AOP： 适用于controller（API），service 方法上（目前需要登录），功能 稍后访问
 */

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class ApiReqLimitAspect {
    private static final String A_KEY_PREFIX = "api_req_limit";
    private static final String API_REQ_LIMIT_LOCK_KEY = "api_req_limit_lock_key";

    private final ObjectMapper objectMapper;
    private final RedisService<String> redisService;
    private final RedissonClient redissonClient;
    private final HttpServletRequest request;

    @SneakyThrows
    @Before(value = "@annotation(apiReqLimit)")
    private void limit(JoinPoint joinPoint, ApiReqLimit apiReqLimit) {
        RLock lock = redissonClient.getLock(API_REQ_LIMIT_LOCK_KEY);
        try {
            lock.lock();
            String headerToken = SysHttpRequestUtil.getUserToken(request);
            String memberId = JwtUtilV2.getMemberId(headerToken);
            if (StringUtils.isEmpty(memberId)) {
                throw new CustomCallException(apiReqLimit.note() + "请登录后再试！");
            }
            ObjectNode actionNode = objectMapper.createObjectNode();
            actionNode.put("content", joinPoint.getSignature().toLongString());
            actionNode.put("memberId", memberId);
            String action = CryptoUtil.md5(actionNode.toString());
            Instant now = Instant.now();
            Optional<String> timeoutStr = redisService.hGet2(A_KEY_PREFIX, action);
            if (timeoutStr.isPresent()) {
                boolean before = now.isBefore(Instant.ofEpochMilli(Long.parseLong(timeoutStr.get())));
                if (before) {
                    // 不允许访问
                    log.debug("不允许访问");
                    log.warn("超出访问频率 {}", actionNode);
                    throw new CustomCallException(apiReqLimit.note() + "请稍后再试！");
                } else {
                    // 可以访问
                    redisService.hDel(A_KEY_PREFIX, action);
                    log.debug("可以访问");
                }
            } else {
                // 可以访问,存储结束时间
                redisService.hSet(A_KEY_PREFIX, action, String.valueOf(now.plusMillis(apiReqLimit.ms()).toEpochMilli()));
                log.debug("可以访问");
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
