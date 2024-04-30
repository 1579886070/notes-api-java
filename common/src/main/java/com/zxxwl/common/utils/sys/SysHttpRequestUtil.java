package com.zxxwl.common.utils.sys;

import com.zxxwl.common.constants.SysConstants;
import jakarta.servlet.http.HttpServletRequest;

public class SysHttpRequestUtil {
    public static String getXSourceId(HttpServletRequest request) {
        return request.getHeader("X-Source-Id");
    }

    public static String getUserToken(HttpServletRequest request) {
        return request.getHeader(SysConstants.REQ_HEAD_USER_TOKEN);
    }


    public static String getSafetyCode(HttpServletRequest request) {
        return request.getHeader(SysConstants.SAFETY_CODE);
    }

    public static String getSafetyToken(HttpServletRequest request) {
        return request.getHeader(SysConstants.SAFETY_TOKEN);
    }
}
