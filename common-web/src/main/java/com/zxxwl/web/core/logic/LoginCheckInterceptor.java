package com.zxxwl.web.core.logic;

import com.zxxwl.SessionContext;
import com.zxxwl.common.utils.Console;
import com.zxxwl.common.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Base64;

//public abstract class LoginCheckInterceptor extends HandlerInterceptorAdapter {
public abstract class LoginCheckInterceptor implements AsyncHandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse response, Object handler) {
        if(req.getMethod().toLowerCase().equals("options"))
            return true;

        boolean rs = hasLogin(SessionContext.getSession(req));
        boolean hasRule = true;
        String servletPath = req.getServletPath();
        if( !rs ){
            if(this.isAjax(req))
                this.noPermissionForAjax(response, 401);
            else
                this.redirect(response, servletPath);
        }
        if (rs) {
            hasRule = hasRule(req, response);
            if (!hasRule)
                onNoPermission(response, req);
        }
        return rs && hasRule;
    }

    protected boolean isAjax(HttpServletRequest request){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    protected void onNoPermission(HttpServletResponse response, HttpServletRequest request){
        if( "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) ){
            this.noPermissionForAjax(response);
            return;
        }

        try {
            response.sendError(403, "Sorry, You have no permission!");
        } catch (IOException e) {
            response.setStatus(403);
        }
    }
    protected void noPermissionForAjax(HttpServletResponse response, int status){
        response.setStatus(status);
        String coded = URLEncoder.encode("对不起，您没有权限", Constants.DefaultCharset);
        if(status == 401)
            coded = URLEncoder.encode("请登录后重试", Constants.DefaultCharset);

        response.setHeader("Access-Control-Expose-Headers",
                "x-api-page,x-api-total,x-api-size,x-api-message,x-api-token,x-api-extra");
        response.setHeader("x-api-message", Base64.getEncoder().encodeToString(coded.getBytes()));
        response.setContentType("application/json; charset=UTF-8");

        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("{}".getBytes(Constants.DefaultCharset));
        } catch (IOException e) {
            Console.bug(e.getMessage());
        }
    }

    protected void noPermissionForAjax(HttpServletResponse response){
        this.noPermissionForAjax(response, 403);
    }

    protected abstract boolean hasLogin(HttpSession session);
    protected abstract void redirect(HttpServletResponse response, String servletPath);
    protected abstract boolean hasRule(HttpServletRequest request, HttpServletResponse response);
}
