package com.example.event;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.dao.GameRepository;
import com.example.dto.message.StatusMessage;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.storage.GameSession;
import com.example.storage.UserSession;

@Component
public class WebSocketEventListener {
    private SimpUserRegistry simpUserRegistry;
    private UserSession userSession;
    private GameSession gameSession;
    @Autowired
    private GameRepository gameDao;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    /* public WebSocketEventListener(SimpUserRegistry simpUserRegistry, UserSession userSession) {
        this.simpUserRegistry = simpUserRegistry;
        this.userSession = userSession;
    } */

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;
        
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        simpMessagingTemplate.convertAndSendToUser(username, "/status", "Connected");
        /* // Retrieve user details from SimpUserRegistry using sessionId
        SimpUser simpUser = simpUserRegistry.getUser(sessionId);
        String username = simpUser != null ? simpUser.getName() : null; */

        /* if (username != null) {
            userSession.addUserSession(username, sessionId);
        } */
        System.out.println("Connect- " + username + " : " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;

        //SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if(headerAccessor.getSessionAttributes() != null) {
            String gameId = (String) headerAccessor.getSessionAttributes().get("gameId");

            if(gameId != null) {
                Game game = gameDao.getGameByGameId(gameId);
                GameStatus currentStatus = game.getStatus();

                String username1 = game.getPlayer1Username();
                String username2 = game.getPlayer2Username();

                if(currentStatus.equals(GameStatus.READY2)) {
                    simpMessagingTemplate.convertAndSendToUser(username1, "/status", "Disconnected");
                    simpMessagingTemplate.convertAndSendToUser(username2, "/status", "Disconnected");
                }
            }
        }

        System.out.println("Disconnect- " + username);
    }
}
