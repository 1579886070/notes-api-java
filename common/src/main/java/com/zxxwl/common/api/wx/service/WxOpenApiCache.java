package com.zxxwl.common.api.wx.service;

/**
 * wx openapi 缓存服务
 *
 * @author qingyu 2023.03.07
 */
public interface WxOpenApiCache {
    /**
     * 获取 AccessToken
     * @param appId appId
     * @return accessToken
     */
    String getAccessToken(String appId);

    /**
     * 缓存 accessToken,并指定过期时间
     *
     * @param accessToken accessToken
     * @param expiresIn   过期时间
     * @return accessToken
     */
    Boolean setAccessToken(String accessToken, long expiresIn, String appId);
    Boolean setUserAccessToken(String openid, String accessToken, long expiresIn);
    Boolean setUserRefreshToken( String openid,String refreshToken, long expiresIn);
    String getUserAccessToken(String openid);
    String getUserRefreshToken(String openid);

}
