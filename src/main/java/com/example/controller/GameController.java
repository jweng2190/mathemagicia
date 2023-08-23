package com.example.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dao.GameRepository;
import com.example.dao.UserRepository;
import com.example.dto.ConnectRequest;
import com.example.model.Game;
import com.example.model.GamePlay;
import com.example.model.GameStatus;
import com.example.model.User;
import com.example.service.GameService;
import com.example.storage.GameStorage;
import com.exception.InvalidGameException;
import com.exception.InvalidParamException;
import com.exception.NotFoundException;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private UserRepository userDao;
    private GameRepository gameDao;
    //private GameStorage gameStorage = GameStorage.getInstance();

    @PostMapping("/create")
    public ResponseEntity<String> createGame(Principal principal,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("time") String time) throws InvalidGameException {
        String username = principal.getName();
        // User currentUser = userDao.getUserByUsername(username);

        /*
         * //check if user has already created a game
         * List<Game> creatorGames = gameService.getGamesByCreator(username);
         * if(creatorGames.size() >= 1) {
         * return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         * }
         */
        // for testing purposes

        Game game = gameService.createGame(username);
        game.setGameDifficulty(difficulty);
        game.setTimeLimit(time);
        gameDao.save(game);
        gameService.setProblems(game);
        String gameId = game.getGameId();
        User user = userDao.getUserByUsername(username);
        user.setActiveGameId(gameId);
        userDao.save(user);
        return ResponseEntity.ok(gameId);
    }

    @GetMapping("/active")
    public ResponseEntity<String> getActiveGameId(Principal principal) {
        String username = principal.getName();
        User user = userDao.getUserByUsername(username);
        if(username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String activeGameId = user.getActiveGameId();
        if(activeGameId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(activeGameId);
    }

    @PostMapping(path="/time_limit", consumes = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<String> getTimeLimit(@RequestBody String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        if(game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String timeLimit = game.getTimeLimit();
        int numMinutes;
        if(timeLimit.equals("5:00")) {
            numMinutes = 5;
        } else if(timeLimit.equals("10:00")) {
            numMinutes = 10;
        } else if(timeLimit.equals("20:00")) {
            numMinutes = 20;
        } else {
            numMinutes = 10;
        }

        return ResponseEntity.ok().body(String.valueOf(numMinutes));
    }

    @PostMapping(path="/game_end", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<String> endGame(@RequestBody Map<String, String> gameInfo) {
        String gameId = gameInfo.get("gameId");
        String playerUsername = gameInfo.get("playerUsername");

        Game game = gameDao.getGameByGameId(gameId);
        if(game.getWinner() == null) {
            String winner = getWinner(game);
            game.setWinner(winner);
        }

        if(game.getGameDate() == null) {
            game.setGameDate(LocalDate.now());
        }
        gameDao.save(game);

        String player1 = game.getPlayer1Username();
        String player2 = game.getPlayer2Username();

        int player1Xp = game.getPlayer1Xp();
        int player2Xp = game.getPlayer2Xp();

        if(playerUsername.equals(player1)) {
            //int originalXp1 = userDao.getXpByUsername(player1);
            //int updatedXp1 = player1Xp + originalXp1;
            User player1User = userDao.getUserByUsername(player1);
            //player1User.setXp(updatedXp1); */
            //userDao.save(player1User);
            saveGame(game, player1User);
        }

        if(playerUsername.equals(player2)) {
            //int originalXp2 = userDao.getXpByUsername(player2);
            //int updatedXp2 = player2Xp + originalXp2;
            User player2User = userDao.getUserByUsername(player2);
            //player2User.setXp(updatedXp2);
            //userDao.save(player2User);
            saveGame(game, player2User);
        }

        return ResponseEntity.ok().body("Game Ended");
    }

    @PostMapping(path="/xp_earned", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Integer> getXpEarned(@RequestBody Map<String, String> playerInfo) {
        String gameId = playerInfo.get("gameId");
        String playerType = playerInfo.get("playerType");

        if(gameId == null || playerType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Game game = gameDao.getGameByGameId(gameId);
        if(playerType.equals("Player 1")) {
            return ResponseEntity.ok().body(game.getPlayer1Xp());
        } else if(playerType.equals("Player 2")) {
            return ResponseEntity.ok().body(game.getPlayer2Xp());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @GetMapping("/type/{id}")
    public ResponseEntity<String> getPlayerType(@PathVariable("id") String gameId, Principal principal) {
        Game game = gameDao.getGameByGameId(gameId);
        String username = principal.getName();
        if(game.getPlayer1Username().equals(username)) {
            return ResponseEntity.ok().body("Player 1");
        } else if(game.getPlayer2Username() == null) {
            if(game.getStatus() == GameStatus.NEW) {
                return ResponseEntity.ok().body("Player 2");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else if(game.getPlayer2Username() != null) {
            if(game.getPlayer2Username().equals(username)) {
                return ResponseEntity.ok().body("Player 2");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/disconnect_num/{id}")
    public ResponseEntity<List<Integer>> getNumDisconnect(@PathVariable("id") String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        if(game != null) {
            return ResponseEntity.ok().body(Arrays.asList(game.getNumDisconnect1(), game.getNumDisconnect2()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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

    private void saveGame(Game game, User user) {
        List<Game> games = user.getGames();
        //add most recent first
        games.add(0, game);
        userDao.save(user);
    }
}