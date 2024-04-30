package com.zxxwl;


import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class LocalSession implements HttpSessionListener {
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        Object token = session.getAttribute(SessionContext.TOKEN_NAME);
        if(token == null)
            return;

        SessionContext.getSessionContext().destroy(token.toString());
    }
}
