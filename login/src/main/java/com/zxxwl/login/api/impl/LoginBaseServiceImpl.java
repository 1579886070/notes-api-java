package com.zxxwl.login.api.impl;


import com.zxxwl.common.api.redis.RedisService;
import com.zxxwl.common.constants.RedisConstants;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.zxxwl.exception.UnauthorizedException;
import com.zxxwl.login.api.service.LoginBaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_MSG_NOTLOGIN;
import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_STATUS_NOTLOGIN;

/**
 * @author PC 2023.05.25
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class LoginBaseServiceImpl implements LoginBaseService {
    private final HttpServletRequest request;
    private final RedisService<String> redisService;

    @Value("${custom.common-web.module-name:base}")
    private String moduleName;

    @Override
    public String currentMemberId() {
        String headerToken = SysHttpRequestUtil.getUserToken(request);
        String memberId = JwtUtilV2.getMemberId(headerToken);
        if (!StringUtils.hasText(memberId)) {
            throw new UnauthorizedException(ACCOUNT_MSG_NOTLOGIN, ACCOUNT_STATUS_NOTLOGIN);
        }
        return memberId;
    }

    @Override
    public boolean logout() {
        String memberId = this.currentMemberId();
        log.info("【用户退出系统，用户id：{}】", memberId);
        return this.logoutByMemberId(memberId);
    }

    @Override
    public boolean logoutByMemberId(String memberId) {
        redisService.del(moduleName + RedisConstants.JWT_TOKEN_INFO + memberId);
        return Boolean.FALSE.equals(redisService.exists(moduleName + RedisConstants.JWT_TOKEN_INFO + memberId));
    }

}
