package com.example.model;

import lombok.Data;

@Data
public class Game {
    private String gameId;
    private User player1;
    private User player2;
    private GameStatus status;
    private User winner;
}
