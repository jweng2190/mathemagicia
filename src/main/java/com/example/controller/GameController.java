package com.example.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.example.dto.message.GameMessage;
import com.example.model.Game;
import com.example.model.GamePlay;
import com.example.model.GameStatus;
import com.example.model.Problem;
import com.example.model.ProblemShort;
import com.example.model.User;
import com.example.service.BotService;
import com.example.service.GameService;
import com.example.storage.GameStorage;
import com.exception.InvalidGameException;
import com.exception.InvalidParamException;
import com.exception.NotFoundException;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final BotService botService;
    private UserRepository userDao;
    private GameRepository gameDao;

    @PostMapping("/create")
    public ResponseEntity<String> createGame(Principal principal,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("time") String time) throws InvalidGameException {
        String username = principal.getName();
        User user = userDao.findByUsername(username);
        if(user.getCreatedGameId() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Game game = gameService.createGame(username, difficulty, time, "regular");
        return ResponseEntity.ok(game.getGameId());
    }

    @PostMapping("/computer")
    public ResponseEntity<List<Object>> createBotGame(Principal principal,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("time") String time,
            @RequestParam("computerLevel") int computerLevel) throws InvalidGameException {
        String username = principal.getName();

        Game game = gameService.createGame(username, difficulty, time, "computer");
        List<Problem> problems = game.getProblemSet();
        String gameId = game.getGameId();
        User user = userDao.findByUsername(username);
        user.setActiveGameId(gameId);
        userDao.save(user);

        int timeMins = parseTime(time);
        List<Object> botInfo = botService.getBotTimes(computerLevel, timeMins, problems);
        int botScore = (int) botInfo.get(0);
        game.setPlayer2Score(botScore);
        return ResponseEntity.ok(Arrays.asList(gameId, computerLevel));
    }


    @GetMapping("/active")
    public ResponseEntity<String> getActiveGameId(Principal principal) {
        String username = principal.getName();
        User user = userDao.findByUsername(username);
        if(username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String activeGameId = user.getActiveGameId();
        if(activeGameId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(activeGameId);
    }

    @GetMapping("players")
    public ResponseEntity<List<String>> getPlayersById(@RequestParam("gameId") String gameId) {
        Game game = gameService.getGameById(gameId);
        List<String> players = Arrays.asList(game.getPlayer1Username(), game.getPlayer2Username());
        if(players.size() == 2) {
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping(path="/active_game", consumes = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<Object>> getActiveGameData(@RequestBody String username) {
        User user = userDao.findByUsername(username);
        String gameId = user.getCreatedGameId();
        if(gameId != null) {
            Game game = gameDao.getGameByGameId(gameId);
            if(game.getStatus() != GameStatus.NEW) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            List<Object> data = new ArrayList<Object>(Arrays.asList(
                game.getGameDifficulty(), game.getTimeLimit(), game.getGameDate(), gameId
            ));

            return ResponseEntity.ok().body(data);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping(path="/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<String> deleteGame(@RequestBody Map<String, String> gameInfo) {
        String username = gameInfo.get("username");
        String gameId = gameInfo.get("gameId");
        if(gameId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Game game = gameDao.getGameByGameId(gameId);
        if(game == null || game.getStatus() != GameStatus.NEW) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        int status = gameService.deleteGame(gameId, username);
        if(status == -1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userDao.findByUsername(username);
        user.setCreatedGameId(null);
        userDao.save(user);
        return ResponseEntity.ok("Game deleted successfully");
    }


    @PostMapping(path="/problem_list", consumes = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<ProblemShort>> getProblemList(@RequestBody String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        if(game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Problem> problems = game.getProblemSet();
        List<ProblemShort> problemShorts = problems.stream()
        .map(problem -> new ProblemShort(problem.getProblemId(), problem.getImage()))
        .collect(Collectors.toList());

        return ResponseEntity.ok().body(problemShorts);
    }

    @PostMapping(path="/bot_stats", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<Object>> getBotStats(@RequestBody Map<String, String> gameInfo) {
        String gameId = gameInfo.get("gameId");
        int computerLevel = Integer.parseInt(gameInfo.get("computerLevel"));

        Game game = gameDao.getGameByGameId(gameId);
        if(game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String timeLimit = game.getTimeLimit();
        int mins = Integer.parseInt(timeLimit.split(":")[0]);
        List<Problem> problems = game.getProblemSet();

        List<Object> botInfo = botService.getBotTimes(computerLevel, mins, problems);
        
        return ResponseEntity.ok().body(botInfo);
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
    public synchronized ResponseEntity<List<Integer>> endGame(@RequestBody Map<String, String> gameInfo) {
        String gameId = gameInfo.get("gameId");
        String playerUsername = gameInfo.get("playerUsername");
        List<Integer> response = gameService.endGame(gameId, playerUsername);

        return ResponseEntity.ok().body(response);
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping(path="/disconnect", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public void handleDisconnect(@RequestBody Map<String, String> playerInfo) {
        String gameId = playerInfo.get("gameId");
        String username = playerInfo.get("username");

        gameService.handleDisconnect(gameId, username);
    }
    

    @GetMapping("/type/{id}")
    public ResponseEntity<Integer> getPlayerType(@PathVariable("id") String gameId, Principal principal) {
        Game game = gameDao.getGameByGameId(gameId);
        String username = principal.getName();
        if(game.getPlayer1Username().equals(username)) {
            return ResponseEntity.ok().body(1);
        } else if(game.getPlayer2Username() == null) {
            if(game.getStatus() == GameStatus.NEW) {
                return ResponseEntity.ok().body(2);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else if(game.getPlayer2Username() != null) {
            if(game.getPlayer2Username().equals(username)) {
                return ResponseEntity.ok().body(2);
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

    public int parseTime(String time) {
        //time is in mins
        return Integer.parseInt(time.split(":")[0]);
    }
}