package com.example.service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.model.Game;
import com.example.model.GamePlay;
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

    public Game createGame(User player) {
        Game game = new Game();
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);
        game.setProblemSet(problemService.getRandomProblems(ProblemService.PROBLEM_SET_SIZE));
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public String getGameCodeByUsername(String player1Username) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            if(allGames.get(key).getPlayer1().getUsername().equals(player1Username)) {
                return key;
            }
        }
        return "";
    }

    /* public Game getGameByUsername(String username) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            String player2Username = allGames.get(key).getPlayer2().getUsername();
            if()
        }
    } */

    public Game getGameById(String gameId) {
        Map<String, Game> allGames = GameStorage.getInstance().getGames();
        for(String key : allGames.keySet()) {
            Game game = allGames.get(key);
            if(game.getGameId().equals(gameId)) {
                return game;
            }
        }
        return null;
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
