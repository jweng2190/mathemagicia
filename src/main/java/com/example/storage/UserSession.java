package com.example.storage;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class UserSession {
    private String gameId;
    private List<String> sessionIds1;
    private List<String> sessionIds2;

    public UserSession() {
        
    }

    public UserSession(String gameId) {
        this.gameId = gameId;
        sessionIds1 = new ArrayList<String>();
        sessionIds2 = new ArrayList<String>();
    }

    public void addUserSession(String playerType, String sessionId) {
        if(playerType.equals("Player 1")) {
            sessionIds1.add(sessionId);
        } else if(playerType.equals("Player 2")) {
            sessionIds2.add(sessionId);
        }
    }

    public void removeUserSession(String playerType, String sessionId) {
        if(playerType.equals("Player 1")) {
            sessionIds1.remove(sessionId);
        } else if(playerType.equals("Player 2")) {
            sessionIds2.remove(sessionId);
        }
    }

    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<String> getSessionIds1() {
        return this.sessionIds1;
    }

    public void setSessionIds1(List<String> sessionIds1) {
        this.sessionIds1 = sessionIds1;
    }

    public List<String> getSessionIds2() {
        return this.sessionIds2;
    }

    public void setSessionIds2(List<String> sessionIds2) {
        this.sessionIds2 = sessionIds2;
    }
}

