package com.zxxwl.common.constants;

import lombok.Getter;

/**
 * 系统状态码
 *
 * @author qingyu
 */
@Getter
public enum SysStatusCodeEmp {
    SUCCESS(200, "操作成功");

    private final int code;
    private final String message;

    SysStatusCodeEmp(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
