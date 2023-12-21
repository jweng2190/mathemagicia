package com.example.dto.message;

public class RematchMessage {
    private String gameId;
    private String playerUsername;
    private String type;
    /* private boolean accepted;
    private long currentTime; */


    public RematchMessage(String type, String gameId, String playerUsername) {
        this.type = type;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
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

    /* public boolean isAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.accepted = isAccepted;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    } */
}
