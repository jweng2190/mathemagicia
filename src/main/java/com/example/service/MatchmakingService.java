package com.example.service;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.dao.UserRepository;
import com.example.dto.message.MatchRequest;
import com.example.dto.message.QuickPlayMessage;
import com.example.model.Game;
import com.example.model.User;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

@Service
public class MatchmakingService {
    private final int MAX_SIZE = 100;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private GameService gs;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private PriorityQueue<MatchRequest> waitingPlayers = new PriorityQueue<MatchRequest>(MAX_SIZE,
    new Comparator<MatchRequest>() {
        public int compare(MatchRequest mr1, MatchRequest mr2) {
            return Long.compare(mr1.getTimestamp(), mr2.getTimestamp());
        }
    });

    public String processRequest(MatchRequest matchRequest) {
        String type = matchRequest.getType();
        if(type.equals("join")) {
            if(!waitingPlayers.contains(matchRequest)) {
                boolean isAdded = waitingPlayers.offer(matchRequest);
                return isAdded ? "success add" : "fail add";
            }
            return "already exists";
        } else if(type.equals("cancel")) {
            boolean isRemoved = waitingPlayers.remove(matchRequest);
            return isRemoved ? "success remove" : "fail remove";
        } else {
            return "invalid request";
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void matchPlayers() {
        while (waitingPlayers.size() >= 2) {
            String player1Username = waitingPlayers.poll().getUsername();
            String player2Username = waitingPlayers.poll().getUsername();
            User player1 = userDao.findByUsername(player1Username);
            User player2 = userDao.findByUsername(player2Username);

            Game game = gs.createQPGame(player1, player2); 
            QuickPlayMessage clientMessage = new QuickPlayMessage("creation", game.getGameId());
            simpMessagingTemplate.convertAndSendToUser(player1.getUsername(), 
            "/quick_play", clientMessage);
            simpMessagingTemplate.convertAndSendToUser(player2.getUsername(), 
            "/quick_play", clientMessage);
        }
    }
}
