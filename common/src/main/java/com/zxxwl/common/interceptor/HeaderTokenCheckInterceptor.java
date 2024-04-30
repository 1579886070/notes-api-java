
package com.zxxwl.common.interceptor;

import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.api.redis.RedisService;
import com.zxxwl.common.constants.RedisConstants;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.zxxwl.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.lang.reflect.Method;

import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_STATUS_NOTLOGIN;


/**
 * HeaderTokenCheckInterceptor
 *
 * @author zhouxin
 * @author qingyu 2023.02.28
 * @apiNote 拦截器中验证Http头中的Token是否正确
 * @since 2020/10/29 10:40
 */

@Slf4j
@Component
public class HeaderTokenCheckInterceptor implements AsyncHandlerInterceptor {
    private final RedisService<String> redisService;
    @Value("${custom.common-web.module-name:base}")
    private String moduleName;

    public HeaderTokenCheckInterceptor(RedisService<String> redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {

            Method method = handlerMethod.getMethod();
            HeaderTokenCheck headerTokenCheck = method.getAnnotation(HeaderTokenCheck.class);

            if (headerTokenCheck != null) {
                String userToken = SysHttpRequestUtil.getUserToken(request);
                if (StringUtils.isNotBlank(userToken)) {
                    String memberId = JwtUtilV2.getMemberId(userToken);
                    if (StringUtils.isNotBlank(memberId)) {
                        String json = redisService.get(RedisConstants.JWT_TOKEN_INFO + memberId);
                        if (json != null) {
                            if (!json.equals(userToken)) {
                                log.warn("【拦截器，redis获取的token和客户端请求头的token不匹配，请求头：[{}]，redis：[{}]】", userToken, json);
                                //  send(response, HttpStatus.UNAUTHORIZED.value(), "未登录或登录已失效，请重新登录！");
                                // return false;
                                throw new UnauthorizedException("未登录或登录已失效，请重新登录！", ACCOUNT_STATUS_NOTLOGIN);
                            }

                            return true;
                        } else {
                            log.warn("【拦截器，redis获取不到memberId，请重新登录！】");
                            // send(response, HttpStatus.UNAUTHORIZED.value(), "未登录或登录已失效，请重新登录！");
                            // return false;
                            throw new UnauthorizedException("未登录或登录已失效，请重新登录！", ACCOUNT_STATUS_NOTLOGIN);
                        }
                    } else {
                        log.warn("【拦截器，JWT解析的memberId为空，请重新登录！】");
                        // send(response, HttpStatus.UNAUTHORIZED.value(), "未登录或登录已失效，请重新登录！");
                        // return false;
                        throw new UnauthorizedException("未登录或登录已失效，请重新登录！", ACCOUNT_STATUS_NOTLOGIN);
                    }
                } else {
                    log.warn("【拦截器，userToken为空，请重新登录！】");
                    // send(response, HttpStatus.UNAUTHORIZED.value(), "未登录或登录已失效，请重新登录！");
                    // return false;
                    throw new UnauthorizedException("未登录或登录已失效，请重新登录！", ACCOUNT_STATUS_NOTLOGIN);
                }
            }

            return true;
        }
        return true;
    }


    /*public void send(HttpServletResponse response, int code, String message) {
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
    }*/

}
