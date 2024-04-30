package com.zxxwl.login.api.service;

/**
 * @author PC 2023.05.25
 */
public interface LoginBaseService {
    /**
     * 查询当前登录用户Id
     *
     * @return memberId
     */
    String currentMemberId();

    /**
     * 退出
     */
    boolean logout();

    boolean logoutByMemberId(String memberId);
}
