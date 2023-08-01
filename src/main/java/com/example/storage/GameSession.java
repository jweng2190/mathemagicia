package com.example.storage;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private List<UserSession> gameSessions;

    public GameSession() {
        gameSessions = new ArrayList<UserSession>();
    }

    public void removeGameSession(String gameId) {
        for(UserSession session: gameSessions) {
            if(session.getGameId().equals(gameId)) {
                gameSessions.remove(session);
            }
        }
    }

    public void addGameSession(UserSession userSession) {
        gameSessions.add(userSession);
    }
}
