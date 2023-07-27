package com.example.event;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.storage.UserSessionMap;

@Component
public class WebSocketEventListener {
    private final SimpUserRegistry simpUserRegistry;
    private final UserSessionMap userSessionMap;
    
    public WebSocketEventListener(SimpUserRegistry simpUserRegistry, UserSessionMap userSessionMap) {
        this.simpUserRegistry = simpUserRegistry;
        this.userSessionMap = userSessionMap;
    }

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        /* // Retrieve user details from SimpUserRegistry using sessionId
        SimpUser simpUser = simpUserRegistry.getUser(sessionId);
        String username = simpUser != null ? simpUser.getName() : null; */

        if (username != null) {
            // Add the user and their sessionId to the UserSessionMap
            userSessionMap.addUserSession(username, sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        /* // Retrieve user details from SimpUserRegistry using sessionId
        SimpUser simpUser = simpUserRegistry.getUser(sessionId);
        String username = simpUser != null ? simpUser.getName() : null; */

        if (username != null) {
            // Remove the user from the UserSessionMap when they disconnect
            userSessionMap.removeUserSession(username);
        }
    }
}
