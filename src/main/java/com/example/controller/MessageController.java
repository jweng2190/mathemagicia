package com.example.controller;

import java.security.Principal;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.dao.GameRepository;
import com.example.dao.ProblemRepository;
import com.example.dao.UserRepository;
import com.example.dto.message.AnswerMessage;
import com.example.dto.message.GameMessage;
import com.example.dto.message.JoinMessage;
import com.example.dto.message.RematchMessage;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.User;
import com.example.service.DifficultyLevelService;
import com.example.service.GameService;
import com.example.service.ProblemService;

@Controller
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private ProblemRepository problemDao;

    @Autowired
    private GameRepository gameDao;

    @Autowired
    private DifficultyLevelService difficultyLevelService;

    @MessageMapping("/game.join")
    public synchronized void joinGame(@Payload JoinMessage message) {
        String playerUsername = message.getPlayerUsername();
        String gameId = message.getGameId();

        Game gameToJoin = gameDao.getGameByGameId(gameId);
        if (gameToJoin == null) {
            GameMessage errorMessage = new GameMessage();
            errorMessage.setType("error");
        } else {
            User player;
            if(playerUsername.equals(gameToJoin.getPlayer1Username())) {
                gameToJoin.setPlayer1Joined(true);
            } else {
                gameToJoin.setPlayer2Joined(true);
                gameToJoin.setPlayer2Username(playerUsername);
            }
            player = userDao.findByUsername(playerUsername);
            player.setActiveGameId(gameId);
            gameDao.save(gameToJoin);

            if(gameToJoin.isPlayer1Joined() && gameToJoin.isPlayer2Joined()) {
                gameToJoin.setStatus(GameStatus.IN_PROGRESS);
            }
            gameDao.save(gameToJoin);
            userDao.save(player);

            GameMessage gameMessage = gameToMessage(gameToJoin);
            gameMessage.setType("join");
            
            sendToUsers(gameToJoin, "/connect", gameMessage);
        }  
    }

    /* @MessageMapping("/game.status")
    public void handleGameStatus(@Payload ClientStatusMessage statusMessage, Principal principal) {
        String username = principal.getName();
        String gameId = statusMessage.getGameId();

        System.out.println("Received status from user " + principal.getName() + ": " + statusMessage.getGameId());
        Game game = gameDao.getGameByGameId(gameId);
        int numD1 = game.getNumDisconnect1();
        int numD2 = game.getNumDisconnect2();

        String playerDisconnect;
        if(game.isPlayer1Disconnect()) {
            playerDisconnect = game.getPlayer1Username();
        } else {
            playerDisconnect = game.getPlayer2Username();
        }

        StatusMessage disconnectMessage = new StatusMessage(gameId, numD1, numD2, null, playerDisconnect);

        if(game.getStatus() == GameStatus.DISCONNECTED) {
            disconnectMessage.setStatus("Disconnected");
            simpMessagingTemplate.convertAndSendToUser(username, "/status", disconnectMessage);
        } else {
            disconnectMessage.setStatus("Connected");
            simpMessagingTemplate.convertAndSendToUser(username, "/status", disconnectMessage);
        }
    } */

    @MessageMapping("/game.rematch")
    public void rematch(@Payload RematchMessage rematchMessage) {
        String gameId = rematchMessage.getGameId();
        String sentPlayer = rematchMessage.getPlayerUsername();
        Game activeGame = gameDao.getGameByGameId(gameId);

        String player1 = activeGame.getPlayer1Username();
        String player2 = activeGame.getPlayer2Username();

        String type = rematchMessage.getRematchType();

        switch(type) {
            case "invite":
                if(sentPlayer.equals(player1)) {
                    simpMessagingTemplate.convertAndSendToUser(player2, "/rematch", Arrays.asList("invite", sentPlayer));
                } else {
                    simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("invite", sentPlayer));
                }
                break;
            case "accept":
                //create new game
                String playerCreate;
                if(sentPlayer.equals(player1)) {
                    playerCreate = player2;
                } else {
                    playerCreate = player1;
                }

                Game rematchGame = gameService.createGame(playerCreate,
                activeGame.getGameDifficulty(), activeGame.getTimeLimit());
                User userCreate = userDao.findByUsername(playerCreate);
                String newGameId = rematchGame.getGameId();
                userCreate.setCreatedGameId(newGameId);
                userDao.save(userCreate);
                
                simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("accept", newGameId));
                simpMessagingTemplate.convertAndSendToUser(player2, "/rematch", Arrays.asList("accept", newGameId));
                break;
            case "reject":
                if(sentPlayer.equals(player1)) {
                    simpMessagingTemplate.convertAndSendToUser(player2, "/rematch", Arrays.asList("reject", sentPlayer));
                } else {
                    simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("reject", sentPlayer));
                }
            case "cancel":
                if(sentPlayer.equals(player1)) {
                    simpMessagingTemplate.convertAndSendToUser(player2, "/rematch", Arrays.asList("cancel", sentPlayer));
                } else {
                    simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("cancel", sentPlayer));
                }
            default:
                String errorMsg = "Invalid rematch request. Please try again later.";
                simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("error", errorMsg));
                simpMessagingTemplate.convertAndSendToUser(player1, "/rematch", Arrays.asList("error", errorMsg));
        } 
    }


    @MessageMapping("/game.answer")
    public void checkAnswer(@Payload AnswerMessage answerMessage) {
        String activeGameId = answerMessage.getGameId();
        Game activeGame = gameDao.getGameByGameId(activeGameId);

        //get both usernames
        String player1Username = activeGame.getPlayer1Username();
        String player2Username = activeGame.getPlayer2Username();

        String playerUsername = answerMessage.getPlayerUsername();

        String userAnswer = answerMessage.getAnswer();
        int currentProblemId = answerMessage.getCurrentProblemId();

        String correctAnswer = problemDao.findAnswerByProblem(currentProblemId);
        String userAnswerTrimmed = userAnswer.trim();

        if(userAnswerTrimmed.equals(correctAnswer)) {
            int problemDifficulty = problemDao.findDifficultyByProblemId(currentProblemId);

            if(playerUsername.equals(player1Username)) {
                int currentPlayerScore = activeGame.getPlayer1Score();
                activeGame.setPlayer1Score(currentPlayerScore + 1);
                int currentPlayerXp = activeGame.getPlayer1Xp();
                int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty);
                activeGame.setPlayer1Xp(currentPlayerXp + xpToAdd);
                activeGame.setIndex1(activeGame.getIndex1() + 1);
            } else if(playerUsername.equals(player2Username)) {
                int currentPlayerScore = activeGame.getPlayer2Score();
                activeGame.setPlayer2Score(currentPlayerScore + 1);
                int currentPlayerXp = activeGame.getPlayer2Xp();
                int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty);;
                activeGame.setPlayer2Xp(currentPlayerXp + xpToAdd);
                activeGame.setIndex2(activeGame.getIndex2() + 1);
            }

            gameDao.save(activeGame);
        }

        //check if either player answered all problems
        if(activeGame.getIndex1() == ProblemService.PROBLEM_SET_SIZE) {
            activeGame.setWinner(player1Username);
        } else if(activeGame.getIndex2() == ProblemService.PROBLEM_SET_SIZE) {
            activeGame.setWinner(player2Username);
        }
        gameDao.save(activeGame);

        GameMessage gameMessage = gameToMessage(activeGame);

        simpMessagingTemplate.convertAndSendToUser(player1Username, "/answer", gameMessage);
        simpMessagingTemplate.convertAndSendToUser(player2Username, "/answer", gameMessage);
    }

    @MessageMapping("/computer")
    public void indivAnswer(@Payload AnswerMessage answerMsg, Principal principal) {
        String gameId = answerMsg.getGameId();
        int currentProblemId = answerMsg.getCurrentProblemId();
        String username = principal.getName();

        Game activeGame = gameDao.getGameByGameId(gameId);

        String userAnswer = answerMsg.getAnswer();
        String correctAnswer = problemDao.findAnswerByProblem(currentProblemId);
        String userAnswerTrimmed = userAnswer.trim();
        int currentPlayerScore = activeGame.getPlayer1Score();

        if(userAnswerTrimmed.equals(correctAnswer)) {
            int problemDifficulty = problemDao.findDifficultyByProblemId(currentProblemId);
            activeGame.setPlayer1Score(currentPlayerScore + 1);
            currentPlayerScore++;
            int currentPlayerXp = activeGame.getPlayer1Xp();
            int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty) / 2;
            activeGame.setPlayer1Xp(currentPlayerXp + xpToAdd);
            gameDao.save(activeGame);
        }

        simpMessagingTemplate.convertAndSendToUser(username, "/bot", currentPlayerScore);
    }

    private void sendToUsers(Game game, String dest, Object msg) {
        String player1 = game.getPlayer1Username();
        String player2 = game.getPlayer2Username();

        if(player1 != null) {
            simpMessagingTemplate.convertAndSendToUser(player1, dest, msg);
        }
        if(player2 != null) {
            simpMessagingTemplate.convertAndSendToUser(player2, dest, msg);
        }
    }
    

    private GameMessage gameToMessage(Game game) {
        GameMessage message = new GameMessage();
        message.setGameId(game.getGameId());
        //message.setPlayer1(game.getPlayer1());
        //message.setPlayer2(game.getPlayer2());
        //message.setPlayer1Joined(game.isPlayer1Joined());
        //message.setPlayer2Joined(game.isPlayer2Joined());
        message.setGameStatus(game.getStatus());
        //message.setProblemSet(game.getProblemSet());
        message.setWinner(game.getWinner());
        message.setScore1(game.getPlayer1Score());
        message.setScore2(game.getPlayer2Score());
        return message;
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
}
