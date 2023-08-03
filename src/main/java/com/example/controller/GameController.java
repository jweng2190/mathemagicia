package com.example.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/create")
    public ResponseEntity<String> createGame(Principal principal) throws InvalidGameException {
        String username = principal.getName();
        //User currentUser = userDao.getUserByUsername(username);

        /* //check if user has already created a game
        List<Game> creatorGames = gameService.getGamesByCreator(username);
        if(creatorGames.size() >= 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } */
        //for testing purposes

        Game game = gameService.createGame(username);
        return ResponseEntity.ok(game.getGameId());
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
}