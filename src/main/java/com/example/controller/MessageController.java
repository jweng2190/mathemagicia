package com.example.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.dao.GameRepository;
import com.example.dao.ProblemRepository;
import com.example.dao.UserRepository;
import com.example.dto.message.AnswerMessage;
import com.example.dto.message.EndMessage;
import com.example.dto.message.GameMessage;
import com.example.dto.message.JoinMessage;
import com.example.dto.message.ReadyMessage;
import com.example.dto.message.RematchMessage;
import com.example.dto.message.StatusMessage;
import com.example.model.Game;
import com.example.model.GameStatus;
import com.example.model.Problem;
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

    @Autowired
    private ProblemRepository problemDao;

    @Autowired
    private GameRepository gameDao;

    private AtomicBoolean player1Finished = new AtomicBoolean(false);
    private AtomicBoolean player2Finished = new AtomicBoolean(false);

    private static ConcurrentMap<List<String>, Long> answerTimestamps = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Long> rematchTimestamps = new ConcurrentHashMap<>();


    @MessageMapping("/game.join")
    @SendTo("/topic/game.state")
    public Object joinGame(@Payload JoinMessage message) {
        String playerUsername = message.getPlayerUsername();
        String gameId = message.getGameId();

        Game gameToJoin = gameDao.getGameByGameId(gameId);
        if (gameToJoin == null) {
            GameMessage errorMessage = new GameMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Error: Unable to enter the game. The game is already full or an internal error has occurred.");
            return errorMessage;
        }
        if(playerUsername.equals(gameToJoin.getPlayer1Username())) {
            GameMessage gameMessage = gameToMessage(gameToJoin);
            gameMessage.setType("game.joined");
            return gameMessage;
        } else {
            if (gameToJoin.getPlayer1Username() != null && gameToJoin.getPlayer2Username() == null) {
                User player2 = userDao.getUserByUsername(playerUsername);
                gameToJoin.setPlayer2Username(player2.getUsername());
                gameToJoin.setPlayer2Joined(true);
                gameToJoin.setStatus(GameStatus.IN_PROGRESS);
            }

            if(!gameToJoin.isPlayer1Ready()) {
                gameToJoin.setPlayer1Ready(false);
            }

            if(!gameToJoin.isPlayer2Ready()) {
                gameToJoin.setPlayer2Ready(false);
            }

            gameDao.save(gameToJoin);

            GameMessage gameMessage = gameToMessage(gameToJoin);
            gameMessage.setType("game.joined");
            return gameMessage;
        }   
    }

    /* @MessageMapping("/game.status")
    @SendTo("/user/game.status")
    public String getStatus(@Payload String statusMessage) {
        if(statusMessage.equals("Disconnected")) {
            return 
        }
    } */


    @MessageMapping("/game.ready")
    @SendTo("/topic/game.ready")
    public Game queueGame(@Payload ReadyMessage readyMessage) {
        User readyUser = userDao.getUserByUsername(readyMessage.getPlayerUsername());
        String gameId = readyMessage.getGameId();
        Game currentGame = gameDao.getGameByGameId(gameId);
        
        if(currentGame.getPlayer1Username().equals(readyUser.getUsername())) {
            currentGame.setPlayer1Ready(true);
        }
        if(currentGame.getPlayer2Username().equals(readyUser.getUsername())) {
            currentGame.setPlayer2Ready(true);
        }

        if(currentGame.isPlayer1Ready() && !currentGame.isPlayer2Ready()) {
            currentGame.setStatus(GameStatus.READY1);
        }
        if(currentGame.isPlayer2Ready() && !currentGame.isPlayer1Ready()) {
            currentGame.setStatus(GameStatus.READY1);
        }
        if(currentGame.isPlayer1Ready() && currentGame.isPlayer2Ready()) {
            currentGame.setStatus(GameStatus.READY2);
        }

        gameDao.save(currentGame);

        return currentGame;
    }


    @MessageMapping("/game.rematch")
    @SendTo("/topic/game.rematch")
    public List<String> rematch(@Payload RematchMessage rematchMessage) {
        String playerUsername = rematchMessage.getPlayerUsername();
        boolean isAccepted = rematchMessage.isAccepted();
        long currentTime = rematchMessage.getCurrentTime();
        String gameId = rematchMessage.getGameId();
        
        Game activeGame = gameDao.getGameByGameId(gameId);

        if(activeGame.getPlayer1Username().equals(playerUsername)) {
            if(isAccepted) {
                activeGame.setPlayer1Rematch(true);
                //player1Rematch.set(true);
                rematchTimestamps.put("Player 1", currentTime);
                if(!activeGame.isPlayer2Rematch()) {
                    gameDao.save(activeGame);
                    return Arrays.asList("REMATCH1", "");
                }
            } else {
                activeGame.setPlayer1Rematch(false);
                //player1Rematch.set(false);
                activeGame.setStatus(GameStatus.FINISHED);
                
            }
            gameDao.save(activeGame);
        }

        if(activeGame.getPlayer2Username().equals(playerUsername)) {
            if(isAccepted) {
                activeGame.setPlayer2Rematch(true);
                //player2Rematch.set(true);
                rematchTimestamps.put("Player 2", currentTime);
                if(!activeGame.isPlayer1Rematch()) {
                    gameDao.save(activeGame);
                    return Arrays.asList("REMATCH1", "");
                }
            } else {
                activeGame.setPlayer2Rematch(false);
                //player2Rematch.set(false);
                activeGame.setStatus(GameStatus.FINISHED);
                
            }
            gameDao.save(activeGame);
        }

        if(activeGame.isPlayer1Rematch() && activeGame.isPlayer2Rematch()) {
            activeGame.setStatus(GameStatus.FINISHED);
            gameDao.save(activeGame);
            String rematchFirst = findFirstRematch();
            String newGameId;
            if(rematchFirst.equals("Player 1")) {
                Game rematchGame = gameService.createGame(activeGame.getPlayer1Username());
                newGameId = rematchGame.getGameId();
            } else {
                Game rematchGame = gameService.createGame(activeGame.getPlayer2Username());
                newGameId = rematchGame.getGameId();
            }
            return Arrays.asList("REMATCH2", newGameId);
        }

        return Arrays.asList("", "");
    }


    @MessageMapping("/game.answer")
    @SendTo("/topic/game.answer")
    public Object checkAnswer(@Payload AnswerMessage answerMessage) {
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
            long timestamp = answerMessage.getTimestamp();
            List<String> answerInfo = new ArrayList<String>();
            answerInfo.add(0, playerUsername);
            answerInfo.add(1, String.valueOf(currentProblemId));
            answerTimestamps.put(answerInfo, timestamp);

            String firstCorrectAnswer = findFirstCorrectAnswer(currentProblemId);
            if(firstCorrectAnswer.equals(player1Username)) {
                int currentPlayerScore = activeGame.getPlayer1Score();
                activeGame.setPlayer1Score(currentPlayerScore + 1);
            } else if(firstCorrectAnswer.equals(player2Username)) {
                int currentPlayerScore = activeGame.getPlayer2Score();
                activeGame.setPlayer2Score(currentPlayerScore + 1);
            }
            //clearTimestamps();

            gameDao.save(activeGame);
        }

        GameMessage gameMessage = gameToMessage(activeGame);
        return gameMessage;
    }

    private static String findFirstCorrectAnswer(int currentProblemId) {
        Set<List<String>> keySet = answerTimestamps.keySet();
        long minTime = System.currentTimeMillis();
        List<String> fastestKey = null;

        for(List<String> key : keySet) {
            if(Integer.parseInt(key.get(1)) == currentProblemId) {
                if(answerTimestamps.get(key) < minTime) {
                    minTime = answerTimestamps.get(key);
                    fastestKey = key;
                }
            }
        }

        if(fastestKey == null) {
            System.out.println("Got null value");
            return "";
        } else {
            return fastestKey.get(0);
        }
    }

    private static String findFirstRematch() {
        Set<String> keySet = rematchTimestamps.keySet();
        long minTime = System.currentTimeMillis();
        String fastestKey = null;

        for(String key : keySet) {
            if(rematchTimestamps.get(key) < minTime) {
                minTime = rematchTimestamps.get(key);
                fastestKey = key;
            }
        }

        if(fastestKey == null) {
            System.out.println("Got null value");
            return "";
        } else {
            return fastestKey;
        }
    }


    @MessageMapping("/game.end")
    @SendTo("/topic/game.end")
    private boolean endGame(@Payload EndMessage endMessage) {
        String activeGameId = endMessage.getGameId();
        Game activeGame = gameDao.getGameByGameId(activeGameId);
        String playerUsername = endMessage.getPlayerUsername();

        String winner = getWinner(activeGame);
        activeGame.setWinner(winner);

        String player1 = activeGame.getPlayer1Username();
        String player2 = activeGame.getPlayer2Username();

        int gameScore1 = activeGame.getPlayer1Score();
        int gameScore2 = activeGame.getPlayer2Score();

        if(playerUsername.equals(player1)) {
            player1Finished.set(true);
            int originalScore1 = userDao.getScoreByUsername(player1);
            int updatedScore1 = gameScore1 + originalScore1;
            userDao.setScoreByUsername(updatedScore1, player1);
            User player1User = userDao.getUserByUsername(player1);
            saveGame(activeGame, player1User);
        }

        if(playerUsername.equals(player2)) {
            player2Finished.set(true);
            int originalScore2 = userDao.getScoreByUsername(player2);
            int updatedScore2 = gameScore2 + originalScore2;
            userDao.setScoreByUsername(updatedScore2, player2);
            User player2User = userDao.getUserByUsername(player2);
            saveGame(activeGame, player2User);
        }

        checkBothPlayersFinished(activeGame);
        return (player1Finished.get() && player2Finished.get());
    }


    private static void clearTimestamps() {
        Set keySet = answerTimestamps.keySet();
        Iterator iterator = keySet.iterator();

        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    

    private GameMessage gameToMessage(Game game) {
        GameMessage message = new GameMessage();
        message.setGameId(game.getGameId());
        //message.setPlayer1(game.getPlayer1());
        //message.setPlayer2(game.getPlayer2());
        message.setPlayer1Joined(game.isPlayer1Joined());
        message.setPlayer2Joined(game.isPlayer2Joined());
        message.setGameStatus(game.getStatus());
        message.setProblemSet(game.getProblemSet());
        //message.setWinner(game.getWinnerUser());
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

    private synchronized void checkBothPlayersFinished(Game game) {
        if (player1Finished.get() && player2Finished.get()) {
            game.setGameDate(LocalDate.now());
            gameDao.save(game);
        }
    }

    private void saveGame(Game game, User user) {
        List<Game> games = user.getGames();
        //add most recent first
        games.add(0, game);
        userDao.save(user);
    }
}
