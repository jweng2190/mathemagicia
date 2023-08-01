package com.example.dto.message;

public class StatusMessage {
    private boolean isConnected;
    private String gameId;
    private String playerUsername;

    public StatusMessage(boolean isConnected, String gameId, String playerUsername) {
        this.isConnected = isConnected;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
    }

    
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
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
