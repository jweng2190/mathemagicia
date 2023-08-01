package com.example.dto.message;

public class GameIdPayload {
    private String gameId;

    public GameIdPayload(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
