package com.example.event;

import java.security.Principal;
import java.util.Timer;
import java.util.TimerTask;

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

        //simpMessagingTemplate.convertAndSendToUser(username, "/status", "Connected");
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

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if(headerAccessor.getSessionAttributes() != null) {
            String gameId = (String) headerAccessor.getSessionAttributes().get("gameId");

            if(gameId != null) {
                Game game = gameDao.getGameByGameId(gameId);
                GameStatus originalStatus = game.getStatus();

                if(originalStatus.equals(GameStatus.READY2)) {
                    String username1 = game.getPlayer1Username();
                    String username2 = game.getPlayer2Username();

                    if (username.equals(username1)) {
                        int numDisconnect1 = game.getNumDisconnect1();
                        game.setNumDisconnect1(numDisconnect1 + 1);
                        game.setStatus(GameStatus.DISCONNECTED);
                        gameDao.save(game);

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // Check if the user has reconnected within 60 seconds
                                if (!(game.getStatus() == GameStatus.READY2)) {
                                    game.setStatus(GameStatus.FINISHED);
                                    gameDao.save(game);
                                    simpMessagingTemplate.convertAndSendToUser(username, "/status",
                                            "Ended by disconnect");
                                }
                            }
                        }, 60000); // 60 seconds in milliseconds
                    } else if (username.equals(username2)) {
                        int numDisconnect2 = game.getNumDisconnect2();
                        game.setNumDisconnect1(numDisconnect2 + 1);
                        game.setStatus(GameStatus.DISCONNECTED);
                        gameDao.save(game);

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // Check if the user has reconnected within 60 seconds
                                if (!(game.getStatus() == GameStatus.READY2)) {
                                    game.setStatus(GameStatus.FINISHED);
                                    gameDao.save(game);
                                    simpMessagingTemplate.convertAndSendToUser(username, "/status",
                                            "Ended by disconnect");
                                }
                            }
                        }, 60000); // 60 seconds in milliseconds
                    }
                    simpMessagingTemplate.convertAndSendToUser(username1, "/status", "Disconnected");
                    simpMessagingTemplate.convertAndSendToUser(username2, "/status", "Disconnected");
                }
            }
        }

        System.out.println("Disconnect- " + username);
    }

    private void runTimer(Game game, String username) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the user has reconnected within 60 seconds
                if (!(game.getStatus() == GameStatus.READY2)) {
                    game.setStatus(GameStatus.FINISHED);
                    gameDao.save(game);
                    simpMessagingTemplate.convertAndSendToUser(username, "/status", "Ended by disconnect");
                }
            }
        }, 60000); // 60 seconds in milliseconds
    }
}
