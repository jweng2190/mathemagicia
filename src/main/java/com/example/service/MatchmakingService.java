package com.example.service;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

@Service
public class MatchmakingService {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private GameService gs;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Queue<User> waitingPlayers = new LinkedList<User>();

    public void addPlayerToQueue(String playerUsername) {
        User user = userDao.findByUsername(playerUsername);
        waitingPlayers.add(user);
    }

    @Scheduled(fixedDelay = 10000)
    public void matchPlayers() {
        while (waitingPlayers.size() >= 2) {
            User player1 = waitingPlayers.poll();
            User player2 = waitingPlayers.poll();

            Game game = gs.createQPGame(player1, player2); 
            simpMessagingTemplate.convertAndSendToUser(player1.getUsername(), 
            "/quick_play", game.getGameId());
            simpMessagingTemplate.convertAndSendToUser(player2.getUsername(), 
            "/quick_play", game.getGameId());
        }
    }
}
