package com.example.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.dao.UserRepository;
import com.example.dto.message.GameMessage;
import com.example.dto.message.JoinMessage;
import com.example.dto.message.ReadyMessage;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.User;
import com.example.service.GameService;
import com.example.storage.GameStorage;

@Controller
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserRepository userDao;

    @MessageMapping("/game.join")
    @SendTo("/topic/game.state")
    public Object joinGame(@Payload JoinMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String player2Username = message.getPlayerUsername();
        GameStorage gameStorage = GameStorage.getInstance();
        ConcurrentHashMap<String, Game> games = (ConcurrentHashMap<String, Game>) gameStorage.getGames();
        Game gameToJoin = null;

        for (Game game : games.values()) {
            if (game.getPlayer1() != null && game.getPlayer2() == null) {
                User player2 = userDao.getUserByUsername(player2Username);
                game.setPlayer2(player2);
                game.setStatus(GameStatus.IN_PROGRESS);
                gameToJoin = game;
            }
        }
        if (gameToJoin == null) {
            GameMessage errorMessage = new GameMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Error: Unable to enter the game. The game is already full or an internal error has occurred.");
            return errorMessage;
        }
        headerAccessor.getSessionAttributes().put("gameId", gameToJoin.getGameId());
        headerAccessor.getSessionAttributes().put("player", message.getPlayerUsername());
        gameToJoin.setPlayer1Ready(false);
        gameToJoin.setPlayer2Ready(false);

        GameMessage gameMessage = gameToMessage(gameToJoin);
        gameMessage.setType("game.joined");
        return gameMessage;
    }

    @MessageMapping("/game.created")
    @SendTo("/topic/game.created")
    public boolean loadGame(@Payload String gameId, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameService.getGameById(gameId);
        return (game == null);
    }

    @MessageMapping("/game.ready")
    @SendTo("/topic/game.ready")
    public Game queueGame(@Payload ReadyMessage readyMessage, SimpMessageHeaderAccessor headerAccessor) {
        User readyUser = userDao.getUserByUsername(readyMessage.getPlayerUsername());
        GameStorage gameStorage = GameStorage.getInstance();
        Game currentGame = gameStorage.getGameByUser(readyUser);
        
        if(currentGame.getPlayer1().getUsername().equals(readyUser.getUsername())) {
            currentGame.setPlayer1Ready(true);
        }
        if(currentGame.getPlayer2().getUsername().equals(readyUser.getUsername())) {
            currentGame.setPlayer2Ready(true);
        }

        if(currentGame.isPlayer1Ready()) {
            currentGame.setStatus(GameStatus.READY1);
        } else if(currentGame.isPlayer2Ready()) {
            currentGame.setStatus(GameStatus.READY1);
        } else if(currentGame.isPlayer1Ready() && currentGame.isPlayer2Ready()) {
            currentGame.setStatus(GameStatus.READY2);
        }

        return currentGame;
    }
    

    private GameMessage gameToMessage(Game game) {
        GameMessage message = new GameMessage();
        message.setGameId(game.getGameId());
        message.setPlayer1(game.getPlayer1());
        message.setPlayer2(game.getPlayer2());
        message.setGameStatus(game.getStatus());
        message.setWinner(game.getWinner());
        return message;
    }
}
