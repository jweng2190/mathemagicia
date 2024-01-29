package com.example.service;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

@Service
public class MatchmakingService {
    private UserRepository userDao;
    private GameService gs;
    private Queue<User> waitingPlayers = new LinkedList<User>();

    public void addPlayerToQueue(String playerUsername) {
        User user = userDao.findByUsername(playerUsername);
        waitingPlayers.add(user);
    }

    public void matchPlayers() {
        while (waitingPlayers.size() >= 2) {
            User player1 = waitingPlayers.poll();
            User player2 = waitingPlayers.poll();

            // Create a match using player1 and player2
            Game game = gs.createQPGame(player1, player2); 
            // Additional logic, e.g., notify players about the match
        }
    }
}
