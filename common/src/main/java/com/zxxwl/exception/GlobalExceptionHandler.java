package com.zxxwl.exception;


import com.zxxwl.common.api.sys.log.service.SysLogService;
import com.zxxwl.common.utils.http.IpUtil;
import com.zxxwl.common.web.http.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;

/**
 * 全局异常处理
 * // 分模块后可能需要 @ComponentScan(basePackages = "com.zxxwl.user.service")
 *
 * @author zhouxin 2020/12/11 21:34
 * @author qingyu 2023-01-03
 */
@Slf4j
@RequiredArgsConstructor
@Order(11)
@CrossOrigin
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {
    private static final String SERVER_ERROR = "系统异常,请联系管理员!";
    private final ErrorAttributes errorAttributes;
    private final SysLogService sysLogService;
    private final ObjectMapper objectMapper;

    /**
     * handleMissingServletRequestPartException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(HttpServletRequest req, Exception e, MissingServletRequestPartException ex) {
        log.error("DetailException:", ex);
        logOutJson(req, e);
        return result(ex.getMessage());
    }

    /**
     * handleMethodArgumentNotValidException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(HttpServletRequest req, Exception e, MethodArgumentNotValidException ex) {
        log.error("DetailException:", ex);
        logOutJson(req, e);
        return result(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * handleBindException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(HttpServletRequest req, Exception e, BindException ex) {
        log.error("DetailException:", ex);
        logOutJson(req, e);
        return result(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * handleMissingServletRequestParameterException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(HttpServletRequest req, Exception e, MissingServletRequestParameterException ex) {
        log.error("DetailException:", ex);
        logOutJson(req, e);
        return result(ex.getParameterName() + "不能为空！");
    }

    /**
     * handleCustomCallException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
    @ExceptionHandler(CustomCallException.class)
    public ResponseEntity<?> handleCustomCallException(HttpServletRequest req, Exception e, CustomCallException ex) {
        log.error("DetailException:", e);
        log.error("CustomException:", ex);
        logOutJson(req, e);
        return result(ex.getMessage());
    }

    /**
     * handleCustomThirdPartyApiException
     * 调用第三方 API 异常 或 失败
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomThirdPartyApiException.class)
    public ResponseEntity<?> handleCustomThirdPartyApiException(HttpServletRequest req, Exception e, CustomThirdPartyApiException ex) {
        log.error("DetailException:", e);
        log.error("ThirdParty  Error:{},Exception:", ex.getMessage(), ex);
        logOutJson(req, e);
        return result(SERVER_ERROR);
    }

    /*
     * handleSysBlacklistNotReleasedException
     *
     * @param req req
     * @param e   e
     * @param ex  ex
     * @return R
     */
   /* @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SysBlacklistNotReleasedException.class)
    public ResponseEntity<?> handleSysBlacklistNotReleasedException(HttpServletRequest req, Exception e, SysBlacklistNotReleasedException ex) {
        log.error("DetailException:", ex);
        logOutJson(req, e);
        return sysBlacklistNotify(ex.getMessage(), ex.getReleaseTime(), ex.getForcedLogOut());
    }*/

    /*
     * 返回系统黑名单通知
     *
     * @param msg          异常信息
     * @param releaseTime  解封时间
     * @param forcedLogOut 是否强制退出
     * @return R
     */
/*    private ResponseEntity<?> sysBlacklistNotify(String msg, LocalDateTime releaseTime, boolean forcedLogOut) {
        SysMbrBlacklistBaseVO vo = new SysMbrBlacklistBaseVO();
        vo.setMsg(msg);
        vo.setReleaseTime(releaseTime);
        vo.setForcedLogOut(forcedLogOut);
        return Response.accessDenied().message(msg).content(vo).send();
    }*/


    /**
     * 400 - Bad Request 请求错误，请修正请求
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(e.getMessage());
    }

    /**
     * 401 - Unauthorized 未经授权 未登录
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(HttpServletRequest req, Exception e, UnauthorizedException ec) {
        log.error("DetailException:", ec);
        log.error("UnauthorizedException.status:{}", ec.getStatus());
        logOutJson(req, ec);
        // return result(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return Response.status(HttpStatus.UNAUTHORIZED.value()).content(Map.of("status", ec.getStatus())).message(ec.getMessage()).send();
    }

    /**
     * 405 - Method Not Allowed 不支持当前请求方法
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(e.getMessage());
    }

    /**
     * 415 - Unsupported Media Type 不支持当前媒体类型
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(e.getMessage());
    }


    /**
     * handleIllegalArgumentException
     *
     * @param e e
     * @return R
     */
    @ResponseStatus(HttpStatus.URI_TOO_LONG)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(HttpStatus.URI_TOO_LONG.value() + HttpStatus.URI_TOO_LONG.getReasonPhrase());
    }


    /**
     * 文件未找到
     *
     * @param req 请求
     * @param e   异常
     * @return R
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result("文件未找到,可能已丢失!");
    }

    /**
     * handleNullPointerException
     *
     * @param req req
     * @param e   e
     * @return R
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(SERVER_ERROR);
    }

    /**
     * handleNoHandlerFoundException
     *
     * @param e e
     * @return R
     * @apiNote <p>需配合 spring.mvc.throw-exception-if-no-handler-found:true 使用<a href="https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/sequence.html#page-title">throwExceptionIfNoHandlerFound</a></p>
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(HttpStatus.NOT_FOUND.value() + HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    /**
     * handleServletException
     *
     * @param e e
     * @return R
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<?> handleServletException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(e.getMessage());
    }

    /**
     * 其他异常 处理
     * 500 - Internal Server Error 服务器内部错误，请联系系统管理员
     *
     * @param req 请求
     * @param e   异常
     * @return R
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> otherException(HttpServletRequest req, Exception e) {
        log.error("DetailException:", e);
        logOutJson(req, e);
        return result(SERVER_ERROR);
    }

    /* UsernameNotFoundException
    // @ResponseStatus
    // @ExceptionHandler(value = UsernameNotFoundException.class)
    // public ResponseEntity<?> handleUsernameNotFoundException(HttpServletRequest req, Exception e) {
    //      log.error("Api:{},Error:{},Exception:", req.getServletPath(), e.getMessage(), e);
    //      logOut(e);
    //      return Response.fail().message( e.getMessage()).send();
    // }
    */

    /**
     * 其他未被处理的异常
     * // @RequestMapping(path = "${server.error.path:${error.path:/error}}")
     *
     * @param req  请求
     * @param resp 响应
     * @param e    异常
     * @return R
     * @deprecated 无用 使用 spring.mvc.throw-exception-if-no-handler-found: true 后，由 handleNoHandlerFoundException 处理
     */
    @Deprecated
    @RequestMapping(path = "/error")
    public ResponseEntity<?> error(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(new ServletWebRequest(req), ErrorAttributeOptions.defaults());
//        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, true);
        log.error("errorAttributes:{}\n DetailException:", errorAttributes, e);
        logOutJson(req, e);
        if (HttpStatus.NOT_FOUND.value() == resp.getStatus()) {
            return result(errorAttributes.get("path").toString());
        } else {
            HttpStatus resolve = HttpStatus.resolve(resp.getStatus());
            if (resolve != null) {
                return result(resolve.getReasonPhrase());
            }
        }
        return result(errorAttributes.toString());
    }


    /**
     * 异常输出
     *
     * @param e java.lang.Exception
     */
    public void logOut(Exception e) {
        logOut("no api", e);
    }

    /**
     * 异常输出
     *
     * @param api api
     * @param e   java.lang.Exception
     */
    public void logOut(String api, Exception e) {
        logOut2(api, e.getMessage(), e.getStackTrace()[0]);
    }

    public void logOut(String api, String ip, Exception e) {
        logOut2(api, e.getMessage(), e.getStackTrace()[0], ip);
    }

    public void logOutJson(HttpServletRequest req, Exception exception) {
        try {
            ObjectNode msgJson = objectMapper.createObjectNode();
            msgJson.put("msg", exception.getMessage())
                    .put("ste0", exception.getStackTrace()[0].toString())
                    .put("api", req.getRequestURL().toString())
                    .put("method", req.getMethod())
                    .put("pathParam", req.getQueryString())
                    .put("contentType", req.getContentType())
                    .put("ip", IpUtil.getIpAddress(req))
                    .put("userAgent", req.getHeader("User-Agent"))
                    .put("referer", req.getHeader("Referer"))
                    /* .put("userToken", req.getHeader("User-Token"))
                    .put("safetyCode", req.getHeader("Safety-Code"))
                    .put("safetyToken", req.getHeader("Safety-Token"))*/
            ;
            log.error("{}", msgJson.toPrettyString());
            log.error("userToken:{},\n safetyCode:{},\n safetyToken:{}", req.getHeader("User-Token"), req.getHeader("Safety-Code"), req.getHeader("Safety-Token"));
            this.saveError(msgJson);
        } catch (Exception e) {
            log.error("异常信息存储失败", e);
        }
    }

    /**
     * 异常输出
     *
     * @param api  api
     * @param msg  msg
     * @param ste0 ste0
     */
    public void logOut2(String api, String msg, StackTraceElement ste0) {
        log.error("Api：{},\n Msg：{},\n StackTraceElement[0]：{}", api, msg, ste0);
        saveError(api, msg, ste0, "no ip");
    }

    public void logOut2(String api, String msg, StackTraceElement ste0, String ip) {
        log.error("Api：{},\n Msg：{},\n StackTraceElement[0]：{}\n ip:{}", api, msg, ste0, ip);
        saveError(api, msg, ste0, ip);
    }

    /**
     * 持久化保存
     *
     * @param api  api
     * @param msg  msg
     * @param ste0 ste0
     */
    private void saveError(String api, String msg, StackTraceElement ste0, String ip) {
        try {
            ObjectNode msgJson = objectMapper.createObjectNode();
            msgJson.put("api", api)
                    .put("msg", msg)
                    .put("ste0", ste0.toString())
                    .put("ip", ip);
            sysLogService.addApiErrorJsonMsg(msgJson);
        } catch (Exception e) {
            log.error("异常信息存储失败", e);
        }
    }

    private void saveError(JsonNode msgJson) {
        try {
            sysLogService.addApiErrorJsonMsg(msgJson);
        } catch (Exception e) {
            log.error("异常信息存储失败", e);
        }
    }

    /**
     * 返回
     *
     * @param msg 异常信息
     * @return R
     */
    private ResponseEntity<?> result(String msg) {
        return Response.fail().message(msg).send();
    }

    private ResponseEntity<?> result(int code, String msg) {
        return Response.status(code).message(msg).send();
    }
}
