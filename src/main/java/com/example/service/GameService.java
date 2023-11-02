package com.example.service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.dao.GameRepository;
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

    @Autowired
    private GameRepository gameDao;

    public Game createGame(String playerUsername, String diff, String time) {
        Game game = new Game();
        //set id
        game.setGameId(UUID.randomUUID().toString());
        //game.setPlayer1(player);
        game.setPlayer1Username(playerUsername);
        game.setPlayer1Joined(true);
        game.setStatus(NEW);

        game.setPlayer1Disconnect(false);
        game.setPlayer2Disconnect(false);

        game.setGameDifficulty(diff);
        game.setTimeLimit(time);

        setProblems(game);

        gameDao.save(game);

        return game;
    }

    public void setProblems(Game game) {
        List<Problem> problems = problemService.getRandomProblems(ProblemService.PROBLEM_SET_SIZE, game);
        game.setProblemSet(problems);
        gameDao.save(game);
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

    public void deleteGame(String gameId) {
        GameStorage.getInstance().deleteGame(gameId);
    }

    /* public Game getGameByUsername(String username) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            String player2Username = allGames.get(key).getPlayer2().getUsername();
            if()
        }
    } */

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

    /* public Game getGameById(String gameId) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            Game game = allGames.get(key);
            if(game.getGameId().equals(gameId)) {
                return game;
            }
        }
        return null;
    } */

    @Cacheable(value = "gameCache", key = "#gameId")
    public Game getGameById(String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        return game;
    }

    /* public Game connectToGame(User player2, String gameId) throws InvalidParamException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id doesn't exist");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game is not valid anymore");
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }
 */
    /* public Game connectToRandomGame(User player2) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW))
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }
    
    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        GameStorage.getInstance().setGame(game);
        return game;
    } */
}
