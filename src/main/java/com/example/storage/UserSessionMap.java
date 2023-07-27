package com.example.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionMap {
    private final Map<String, String> userSessionMap;

    public UserSessionMap() {
        this.userSessionMap = new ConcurrentHashMap<>();
    }

    public void addUserSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void removeUserSession(String username) {
        userSessionMap.remove(username);
    }

    public String getSessionIdByUsername(String username) {
        return userSessionMap.get(username);
    }

    public boolean containsUsername(String username) {
        return userSessionMap.containsKey(username);
    }
}

