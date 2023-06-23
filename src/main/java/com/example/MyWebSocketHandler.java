package com.example;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final int MAX_USERS = 2;
    private static final AtomicInteger userCount = new AtomicInteger(0);
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(userCount.incrementAndGet() <= MAX_USERS) {
            //allow the connection
            sessions.add(session);
            String sessionId = session.getId();

            String gameCode = (String) session.getAttributes().get("gameCode");

            System.out.println("New WebSocket session established. Session ID: " + sessionId + "\n Game Code: " + gameCode);
            session.sendMessage(new TextMessage("Server: Welcome!"));

            String player = getPlayerFromSession(session);
            session.getAttributes().put("player", player);
        } else {
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String receivedMessage = message.getPayload();
        String player = getPlayerFromSession(session);

        String responseText = "Player " + player + " sent the message: " + receivedMessage;

        // Create a TextMessage object with the response content
        TextMessage responseMessage = new TextMessage(responseText);

        // Send the response message back to the client
        session.sendMessage(responseMessage);
    }

    public String getPlayerFromSession(WebSocketSession session) {
        Object playerObject = session.getAttributes().get("player");

        if (playerObject == null) {
            playerObject = session.getPrincipal().getName();
        }

        return (String) playerObject;
    }
}
