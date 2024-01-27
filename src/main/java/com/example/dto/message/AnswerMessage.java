package com.example.dto.message;

public class AnswerMessage {
    private String type;
    private String gameId;
    private String playerUsername;
    private String answer;
    private int currentProblemIndex;
    private int currentProblemId;
    private long timestamp;


    public AnswerMessage() {
    } 

    public AnswerMessage(String type, String gameId, String playerUsername, String answer, int currentProblemIndex,
    int currentProblemId, long timestamp) {
        this.type = type;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
        this.answer = answer;
        this.currentProblemIndex = currentProblemIndex;
        this.currentProblemId = currentProblemId;
        this.timestamp = timestamp;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getCurrentProblemIndex() {
        return this.currentProblemIndex;
    }

    public void setCurrentProblemIndex(int currentProblemIndex) {
        this.currentProblemIndex = currentProblemIndex;
    }

    public int getCurrentProblemId() {
        return this.currentProblemId;
    }

    public void setCurrentProblemId(int currentProblemId) {
        this.currentProblemId = currentProblemId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
