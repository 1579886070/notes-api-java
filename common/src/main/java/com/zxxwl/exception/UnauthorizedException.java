package com.zxxwl.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_STATUS_UNKNOW;
import static com.zxxwl.common.constants.SysLoginConstants.SUPPORT_ACCOUNT_STATUS;

/**
 * 未登录
 *
 * @author qingyu
 */
@Slf4j
public class UnauthorizedException extends RuntimeException {
    /**
     * 错误信息
     */
    private final String message;
    /**
     * `NotLogIn`=未登录，`Unregistered`=未注册,`Disabled`=已禁用,`Unknow`=未知
     */
    @Getter
    private final String status;
    /**
     * 错误码
     */
    private final int code = HttpStatus.UNAUTHORIZED.value();

    /**
     * FIXME 一般情况不建议使用
     */
    public UnauthorizedException() {
        super("未登录或登录已失效，请重新登录！");
        this.message = super.getMessage();
        this.status = ACCOUNT_STATUS_UNKNOW;
    }

    /**
     * FIXME 一般情况不建议使用
     *
     * @param message message
     */
    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
        this.status = ACCOUNT_STATUS_UNKNOW;
    }

    public UnauthorizedException(String message, String status) {
        super(message);
        this.message = message;
        if (!SUPPORT_ACCOUNT_STATUS.contains(status)) {
            log.warn("不受支持的状态：{}", status);
            this.status = ACCOUNT_STATUS_UNKNOW;
        } else {
            this.status = status;
        }
    }
}
