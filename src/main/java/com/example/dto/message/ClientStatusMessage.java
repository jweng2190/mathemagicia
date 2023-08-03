package com.example.dto.message;

public class ClientStatusMessage {
    private String gameId;
    private String playerUsername;

    public ClientStatusMessage(String gameId, String playerUsername) {
        this.gameId = gameId;
        this.playerUsername = playerUsername;
    }

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    public String getPlayerUsername() {
        return playerUsername;
    }
    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }
}
