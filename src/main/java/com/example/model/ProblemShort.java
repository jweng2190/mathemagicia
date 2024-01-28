package com.example.model;

public class ProblemShort {
    private int problemId;
    private String image;

    public ProblemShort() {
        
    }

    public ProblemShort(int problemId, String image) {
        this.problemId = problemId;
        this.image = image;
    }

    public int getProblemId() {
        return this.problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
