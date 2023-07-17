package com.example.dto.message;

public class RematchMessage {
    private String type;
    private String gameId;
    private String playerUsername;
    private boolean accepted;

    public RematchMessage(String type, String gameId, String playerUsername, boolean isAccepted) {
        this.type = type;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
        this.accepted = isAccepted;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerUsername() {
        return this.playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.accepted = isAccepted;
    }
}
