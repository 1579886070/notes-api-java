package com.zxxwl.common.api.wx.service;



import com.zxxwl.common.constants.WxConstants;
import com.zxxwl.common.api.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qingyu
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class WxOpenApiCacheImpl implements WxOpenApiCache {
    private final RedisService<String> redisService;

    @Override
    public String getAccessToken(String appId) {
        return redisService.get(WxConstants.CACHE_KEY_WX_CGI_BIN_TOKEN + appId);
    }

    @Override
    public Boolean setAccessToken(String accessToken, long expiresIn, String appId) {
        return redisService.set(WxConstants.CACHE_KEY_WX_CGI_BIN_TOKEN + appId, accessToken,300);
    }

    @Override
    public Boolean setUserAccessToken(String accessToken, String openid, long expiresIn) {
        return redisService.set(WxConstants.CACHE_KEY_USER_ACCESS_TOKEN + openid, accessToken, expiresIn);
    }

    @Override
    public Boolean setUserRefreshToken(String refreshToken, String openid, long expiresIn) {
        return redisService.set(WxConstants.CACHE_KEY_USER_REFRESH_TOKEN + openid, refreshToken, 86400 * 30);
    }

    @Override
    public String getUserAccessToken(String openid) {
        return redisService.get(WxConstants.CACHE_KEY_USER_ACCESS_TOKEN + openid);
    }

    @Override
    public String getUserRefreshToken(String openid) {
        return redisService.get(WxConstants.CACHE_KEY_USER_ACCESS_TOKEN + openid);
    }
}
