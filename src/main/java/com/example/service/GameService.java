package com.example.service;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.dao.GameRepository;
import com.example.dao.UserRepository;
import com.example.dto.message.GameMessage;
import com.example.model.Game;
import com.example.model.GamePlay;
import com.example.model.GameStatus;
import com.example.model.Problem;
import com.example.model.User;
import com.example.storage.GameStorage;
import com.exception.InvalidGameException;
import com.exception.InvalidParamException;
import com.exception.NotFoundException;

import static com.example.model.GameStatus.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameService {
    private ProblemService problemService;
    private XpLevelService xpLevelService;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GameRepository gameDao;

    @Autowired
    private UserRepository userDao;

    public Game createGame(String playerUsername, String diff, String time, String type) {
        Game game = new Game();
        //set id
        game.setGameId(UUID.randomUUID().toString());
        //game.setPlayer1(player);
        game.setPlayer1Username(playerUsername);
        //game.setPlayer1Joined(true);
        game.setStatus(NEW);

        game.setGameDifficulty(diff);
        game.setTimeLimit(time);
        game.setGameDate(LocalDate.now());

        String status1 = ""; String status2 = "";
        int n = ProblemService.PROBLEM_SET_SIZE;
        for(int i = 0; i < n; i++) {
            if(i != n - 1) {
                status1 += "-1,";
                status2 += "-1,";
            } else {
                status1 += "-1";
                status2 += "-1";
            }
        }

        game.setStatusProb1(status1);
        game.setStatusProb2(status2);

        setProblems(game);

        User user = userDao.findByUsername(playerUsername);
        String gameId = game.getGameId();
        if(type.equals("live")) {
            user.setCreatedGameId(gameId);
            game.setType(type);
        }

        gameDao.save(game);  
        userDao.save(user);

        return game;
    }

    public int deleteGame(String gameId, String username) {
        Game game = gameDao.getGameByGameId(gameId);
        if(!game.getPlayer1Username().equals(username)) {
            return -1;
        }

        gameDao.deleteByGameId(gameId);
        return 0;
    }

    public synchronized List<Integer> endGame(String gameId, String playerUsername) {
        Game game = gameDao.getGameByGameId(gameId);
        game.setStatus(GameStatus.FINISHED);
        User user = userDao.findByUsername(playerUsername);
        user.setActiveGameId(null);
        if(game.getPlayer1Username().equals(playerUsername)) {
            user.setCreatedGameId(null);
            user.setProblemsSolved(user.getProblemsSolved() + game.getPlayer1Score());
        } else {
            user.setProblemsSolved(user.getProblemsSolved() + game.getPlayer2Score());
        }

        if(game.getWinner() == null) {
            String winner = getWinner(game);
            game.setWinner(winner);
            user.setGamesWon(user.getGamesWon() + 1);
        }

        if(game.getGameDate() == null) {
            game.setGameDate(LocalDate.now());
        }
        gameDao.save(game);

        String player1 = game.getPlayer1Username();
        String player2 = game.getPlayer2Username();

        int player1Xp = game.getPlayer1Xp();
        int player2Xp = game.getPlayer2Xp();

        int currXp = user.getXp();
        int level = user.getLevel();
        int xpThreshold = xpLevelService.getXpToLevelUp(level);
        List<Integer> newXpData;

        int xpAdd;
        if(playerUsername.equals(player1)) {
            newXpData = xpLevelService.levelUp(currXp, level, player1Xp);
            xpAdd = player1Xp;
        } else {
            newXpData = xpLevelService.levelUp(currXp, level, player2Xp);
            xpAdd = player2Xp;
        }

        int newLevel = newXpData.get(0);
        int newXp = newXpData.get(1);
        int newThreshold = newXpData.get(2);
        user.setLevel(newLevel);
        user.setXp(newXp);
        userDao.save(user);
        saveGame(game, user);

        return Arrays.asList(level, currXp, xpThreshold, newLevel, newXp, newThreshold, xpAdd);
    }

    public synchronized void handleDisconnect(String gameId, String username) {
        Game game = gameDao.getGameByGameId(gameId);
        GameStatus status = game.getStatus();
        User user = userDao.findByUsername(username);
        switch(status) {
            case NEW:
                if(game.getPlayer1Username().equals(username)) {
                    game.setPlayer1Joined(false);
                } else {
                    game.setPlayer2Username(null);
                    game.setPlayer2Joined(false);
                    simpMessagingTemplate.convertAndSendToUser(game.getPlayer1Username(), 
                    "/created_status", "disconnect");
                }
                
                user.setActiveGameId(null);
                gameDao.save(game);
                userDao.save(user);
                System.out.println("Disconnect: " + gameId);
                break;
            case FINISHED:
                user.setActiveGameId(null);
                if(game.getPlayer1Username().equals(username)) {
                    user.setCreatedGameId(null);
                }
                userDao.save(user);
                break;
            default:
                endGame(gameId, username);
                GameMessage gm = new GameMessage();
                gm.setGameId(gameId);
                gm.setType("disconnect");
                simpMessagingTemplate.convertAndSendToUser(game.getPlayer1Username(), "/connect", gm);
                simpMessagingTemplate.convertAndSendToUser(game.getPlayer2Username(), "/connect", gm);
                break;
        }
    }

    public void setProblems(Game game) {
        List<Problem> problems = problemService.getRandomProblems(ProblemService.PROBLEM_SET_SIZE, game);
        game.setProblemSet(problems);
        gameDao.save(game);
    }

    private void saveGame(Game game, User user) {
        List<Game> games = user.getGames();
        //add most recent first
        games.add(0, game);
        userDao.save(user);
    }

    public String getWinner(Game game) {
        if(game.getPlayer1Score() > game.getPlayer2Score()) {
            return game.getPlayer1Username();
        } else if (game.getPlayer1Score() < game.getPlayer2Score()) {
            return game.getPlayer2Username();
        } else {
            return null;
        }
    }

    public String getGameCodeByUsername(String player1Username) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            if(allGames.get(key).getPlayer1Username().equals(player1Username)) {
                return key;
            }
        }
        return "";
    }

    public List<Game> getGamesByCreator(String creatorUsername) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        List<Game> gameListByUser = new ArrayList<Game>();
        int i = 0;

        for(String key : allGames.keySet()) {
            Game game = allGames.get(key);
            if(game.getPlayer1Username().equals(creatorUsername) && game.getStatus() != GameStatus.FINISHED) {
                gameListByUser.add(i, game);
                i++;
            }
        }

        return gameListByUser;
    }

    @Cacheable(value = "gameCache", key = "#gameId")
    public Game getGameById(String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        return game;
    }
}
