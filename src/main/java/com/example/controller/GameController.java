package com.example.controller;

import java.security.Principal;
import java.util.List;

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

import com.example.dao.UserRepository;
import com.example.dto.ConnectRequest;
import com.example.model.Game;
import com.example.model.GamePlay;
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
    //private GameStorage gameStorage = GameStorage.getInstance();

    @GetMapping("/create")
    public ResponseEntity<String> createGame(Principal principal) throws InvalidGameException {
        String username = principal.getName();
        User currentUser = userDao.getUserByUsername(username);

        //check if user has already created a game
        List<Game> creatorGames = gameService.getGamesByCreator(username);
        if(creatorGames.size() >= 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Game game = gameService.createGame(currentUser);
        return ResponseEntity.ok(game.getGameId());
    }

    @GetMapping("/created/{id}")
    public ResponseEntity<String> isCreatedWithId(@PathVariable("id") String gameId, Principal principal) {
        Game game = gameService.getGameById(gameId);
        String currentUsername = principal.getName();
        return ResponseEntity.ok(String.valueOf(game.getPlayer1().getUsername().equals(currentUsername)));
    }

    /* @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody User player) {
        log.info("start game request: {}", player);
        return ResponseEntity.ok(gameService.createGame(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
    } */

    /* @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody User player) throws NotFoundException {
        log.info("connect random {}", player);
        return ResponseEntity.ok(gameService.connectToRandomGame(player));
    } */

    /* @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    } */
}