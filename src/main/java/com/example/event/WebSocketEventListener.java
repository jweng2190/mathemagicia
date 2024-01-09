package com.example.event;

import java.security.Principal;
import java.time.LocalDate;
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
import com.example.dao.UserRepository;
import com.example.dto.message.StatusMessage;
import com.example.dto.message.ClientStatusMessage;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.User;
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
    private UserRepository userDao;
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

    /* @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if(headerAccessor.getSessionAttributes() != null) {
            String gameId = (String) headerAccessor.getSessionAttributes().get("gameId");
            String type = (String) headerAccessor.getSessionAttributes().get("type");

            if (type != null && gameId != null) {
                Game game = gameDao.getGameByGameId(gameId);
                GameStatus originalStatus = game.getStatus();

                if (originalStatus.equals(GameStatus.IN_PROGRESS)) {
                    String username1 = game.getPlayer1Username();
                    String username2 = game.getPlayer2Username();

                    if (username.equals(username1)) {
                        int numDisconnect1 = game.getNumDisconnect1();
                        game.setNumDisconnect1(numDisconnect1 + 1);
                        game.setStatus(GameStatus.DISCONNECTED);
                        game.setPlayer1Disconnect(true);
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
                        game.setPlayer2Disconnect(true);
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
                    game.setStatus(GameStatus.DISCONNECTED);
                    User player1 = userDao.findByUsername(username1);
                    User player2 = userDao.findByUsername(username2);

                    player1.setActiveGameId(null);
                    player2.setActiveGameId(null);

                    userDao.save(player1);
                    userDao.save(player2);

                    String winner = getWinner(game, username2);
                    game.setWinner(winner);
                    game.setGameDate(LocalDate.now());
                    gameDao.save(game);
                    
                    StatusMessage disconnectMessage = new StatusMessage(gameId, game.getNumDisconnect1(),
                            game.getNumDisconnect2(), "Disconnected", username);
                    simpMessagingTemplate.convertAndSendToUser(username1, "/status", disconnectMessage);
                    simpMessagingTemplate.convertAndSendToUser(username2, "/status", disconnectMessage);
                }
            }
        }

        System.out.println("Disconnect- " + username);
    } */

    /* @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        String username = principal != null ? principal.getName() : null;
        if(username == null) {
            return;
        }

        User player = userDao.findByUsername(username);
        String gameId = player.getActiveGameId();
        Game game = gameDao.getGameByGameId(gameId);

        GameStatus gameStatus = game.getStatus();
        if(gameStatus == GameStatus.NEW) {
            if(username.equals(game.getPlayer1Username())) {
                game.setPlayer1Joined(false);
                System.out.println("Player 1 Not Ready");
            } else {
                game.setPlayer2Joined(false);
                System.out.println("Player 2 Not Ready");
            }
            player.setActiveGameId(null);
        } else if(gameStatus == GameStatus.IN_PROGRESS) {

        } else if(gameStatus == GameStatus.FINISHED) {

        }
        gameDao.save(game);
        userDao.save(player);
    } */

    public String getWinner(Game game, String username) {
        String username1 = game.getPlayer1Username();
        String username2 = game.getPlayer2Username();

        if(username1.equals(username)) {
            return username2;
        } else if(username2.equals(username)) {
            return username1;
        } else {
            return null;
        }
    }
}
