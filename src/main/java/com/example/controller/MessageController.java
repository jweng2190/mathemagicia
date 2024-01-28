package com.example.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.dao.GameRepository;
import com.example.dao.ProblemRepository;
import com.example.dao.UserRepository;
import com.example.dto.message.AnswerMessage;
import com.example.dto.message.GameAnswer;
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
                simpMessagingTemplate.convertAndSendToUser(gameToJoin.getPlayer1Username(),
                "/created_status", "joined");
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
                activeGame.getGameDifficulty(), activeGame.getTimeLimit(), "regular");
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
    public synchronized void checkAnswer(@Payload AnswerMessage answerMessage) {
        String gameId = answerMessage.getGameId();
        Game game = gameDao.getGameByGameId(gameId);

        String playerUsername = answerMessage.getPlayerUsername();
        int playerType = playerTypeByGame(game, playerUsername);
        String player1Username = game.getPlayer1Username();
        String player2Username = game.getPlayer2Username();

        String userAnswer = answerMessage.getAnswer();
        int currentProblemId = answerMessage.getCurrentProblemId();
        int currentProblemIndex = answerMessage.getCurrentProblemIndex();

        String status1 = game.getStatusProb1();
        String status2 = game.getStatusProb2();

        String correctAnswer = problemDao.findAnswerByProblem(currentProblemId);
        String userAnswerTrimmed = userAnswer.trim();

        GameAnswer gameAnswer;
        int problemDifficulty = problemDao.findDifficultyByProblemId(currentProblemId);
        if(playerType == 1) {
            if(getStatusByIndex(status1, currentProblemIndex) != 1) {
                if(userAnswerTrimmed.equals(correctAnswer)) {
                    status1 = setStatusByIndex(status1, currentProblemIndex, 1);
                    game.setStatusProb1(status1);
                    int currentPlayerScore = game.getPlayer1Score();
                    game.setPlayer1Score(currentPlayerScore + 1);
                    int currentPlayerXp = game.getPlayer1Xp();
                    int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty);
                    game.setPlayer1Xp(currentPlayerXp + xpToAdd);
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    1, playerType);
                } else {
                    status1 = setStatusByIndex(status1, currentProblemIndex, 0);
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                0, playerType);
                }
            } else {
                if(userAnswerTrimmed.equals(correctAnswer)) {
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    1, playerType); 
                } else {
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    0, playerType); 
                }
            }
        } else {
            if(getStatusByIndex(status2, currentProblemIndex) != 1) {
                if(userAnswerTrimmed.equals(correctAnswer)) {
                    status2 = setStatusByIndex(status2, currentProblemIndex, 1);
                    game.setStatusProb2(status2);
                    int currentPlayerScore = game.getPlayer2Score();
                    game.setPlayer2Score(currentPlayerScore + 1);
                    int currentPlayerXp = game.getPlayer2Xp();
                    int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty);
                    game.setPlayer2Xp(currentPlayerXp + xpToAdd);
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    1, playerType);
                } else {
                    status2 = setStatusByIndex(status2, currentProblemIndex, 0);
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                0, playerType);
                }
            } else {
                if(userAnswerTrimmed.equals(correctAnswer)) {
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    1, playerType); 
                } else {
                    gameAnswer = new GameAnswer(game.getPlayer1Score(), game.getPlayer2Score(),
                    0, playerType); 
                }
            }
        }

        gameDao.save(game);

        if(game.getPlayer1Score() == ProblemService.PROBLEM_SET_SIZE) {
            gameAnswer.setWinner(player1Username);
        } else if(game.getPlayer2Score() == ProblemService.PROBLEM_SET_SIZE) {
            gameAnswer.setWinner(player2Username);
        }

        simpMessagingTemplate.convertAndSendToUser(player1Username, "/answer", gameAnswer);
        simpMessagingTemplate.convertAndSendToUser(player2Username, "/answer", gameAnswer);
    }

    @MessageMapping("/computer")
    public void indivAnswer(@Payload AnswerMessage answerMsg) {
        String type = answerMsg.getType();
        String gameId = answerMsg.getGameId();
        Game activeGame = gameDao.getGameByGameId(gameId);
        if(type.equals("bot")) {
            activeGame.setPlayer2Score(activeGame.getPlayer2Score() + 1);
            if(activeGame.getPlayer2Score() == ProblemService.PROBLEM_SET_SIZE) {
                activeGame.setWinner("computer won");
            }
            gameDao.save(activeGame);
            List<Object> gameData = Arrays.asList(activeGame.getPlayer2Score(), -1, activeGame.getWinner(), 2);
            simpMessagingTemplate.convertAndSendToUser(activeGame.getPlayer1Username(), "/bot", gameData);
        } else {
            int currentProblemId = answerMsg.getCurrentProblemId();
            int currentProblemIndex = answerMsg.getCurrentProblemIndex();
            String username = answerMsg.getPlayerUsername();

            String userAnswer = answerMsg.getAnswer();
            String correctAnswer = problemDao.findAnswerByProblem(currentProblemId);
            String userAnswerTrimmed = userAnswer.trim();
            int currentPlayerScore = activeGame.getPlayer1Score();
            String status1 = activeGame.getStatusProb1();
            int status;

            if(getStatusByIndex(status1, currentProblemIndex) != 1) {
                String newStatus;
                if(userAnswerTrimmed.equals(correctAnswer)) {
                    newStatus = setStatusByIndex(status1, currentProblemIndex, 1);
                    int problemDifficulty = problemDao.findDifficultyByProblemId(currentProblemId);
                    activeGame.setPlayer1Score(currentPlayerScore + 1);
                    currentPlayerScore++;
                    status = 1;
                    int currentPlayerXp = activeGame.getPlayer1Xp();
                    int xpToAdd = difficultyLevelService.getXpForDifficultyLevel(problemDifficulty) / 2;
                    activeGame.setPlayer1Xp(currentPlayerXp + xpToAdd);
                    gameDao.save(activeGame);
                } else {
                    newStatus = setStatusByIndex(status1, currentProblemIndex, 0);
                    status = 0;
                }
                activeGame.setStatusProb1(newStatus);
            } else {
                status = (userAnswerTrimmed.equals(correctAnswer)) ? 1 : 0;
            }
            
            if(activeGame.getPlayer1Score() == ProblemService.PROBLEM_SET_SIZE) {
                activeGame.setWinner(activeGame.getPlayer1Username());
            }
            gameDao.save(activeGame);

            List<Object> gameData = Arrays.asList(currentPlayerScore, status, activeGame.getWinner(), 1);
    /*         Map<Object, Object> m = Stream.of(new Object[][] {
                {currentPlayerScore, status, activeGame.getWinner()}
            }).collect(Collectors.toMap(data -> (Integer)data[0], data -> (Integer)data[1])); */

            simpMessagingTemplate.convertAndSendToUser(username, "/bot", gameData);
        }
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

    public static int playerTypeByGame(Game game, String username) {
        return (game.getPlayer1Username().equals(username)) ? 1 : 2;
    }

    public static int getStatusByIndex(String s, int i) {
        String[] values = s.split(",");
        if (i >= 0 && i < values.length) {
            return Integer.parseInt(values[i]);
        } else {
            return -1;
        }
    }

    public static String setStatusByIndex(String s, int i, int status) {
        String[] values = s.split(",");
        if (i >= 0 && i < values.length) {
            values[i] = String.valueOf(status);
            String modifiedString = String.join(",", values);
            return modifiedString;
        } else {
            return s;
        }
    }
}
