package com.zxxwl;

import com.zxxwl.common.utils.Console;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import java.util.HashMap;
import java.util.Map;


public class SessionContext {
    public static final String TOKEN_NAME = "token";
    private static final int SESSION_LIFE_TIME = 7200;

    private static SessionContext context;
    private Map<String, HttpSession> sessions;

    private SessionContext(){
        this.sessions = new HashMap<>();
    }

    public static SessionContext getSessionContext(){
        if(context != null)
            return context;

        context = new SessionContext();

        return context;
    }

    public static HttpSession getSession(HttpServletRequest request){
        SessionContext context = SessionContext.getSessionContext();

        String token = request.getHeader(SessionContext.TOKEN_NAME);
        if(token == null)
            token = "";

        if(token.length() < 1)
            token = request.getRequestedSessionId();

        HttpSession session = null;
        if(context.hasSession(token))
            session = context.getSession(token);
        else
            session = request.getSession();

        return session;
    }

    public boolean hasSession(String id){
        return this.sessions.containsKey(id);
    }

    public synchronized void addSession(HttpServletRequest request){
        String token = request.getHeader("token");
        if(token == null || token.length() < 1)
            token = request.getRequestedSessionId();

        this.addSession(token, request.getSession());
    }

    public synchronized void addSession(String id, HttpSession session){
        session.setMaxInactiveInterval(SESSION_LIFE_TIME);
        this.sessions.put(id, session);
    }

    public HttpSession getSession(String id){
        return this.sessions.getOrDefault(id, null);
    }

    public void destroy(String id){
        HttpSession session = this.getSession(id);
        if(session == null)
            return;

        this.sessions.remove(id);
        try{
            session.invalidate();
        }catch (IllegalStateException e){
            Console.log(e.getMessage());
        }
    }
}
