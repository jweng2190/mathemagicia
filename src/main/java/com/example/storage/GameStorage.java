package com.example.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.User;

public class GameStorage {
    private static Map<String, Game> games;
    private static Map<String, String> waitingPlayers;
    private static GameStorage instance;
    private UserRepository userDao;

    public GameStorage() {
        games = new ConcurrentHashMap<>();
        waitingPlayers = new ConcurrentHashMap<>();
    }

    public static synchronized GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }
        return instance;
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public Game getGameByUser(User user) {
        for(Game game : games.values()) {
            if(game.getPlayer1().getUsername().equals(user.getUsername()) || 
            game.getPlayer2().getUsername().equals(user.getUsername())) {
                return game;
            }
        }
        return null;
    }

    public void setGame(Game game) {
        games.put(game.getGameId(), game);
    }
}
