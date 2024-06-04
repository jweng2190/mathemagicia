package com.example.model;

public class ProblemShort {
    private int problemId;
    private String image;
    private String description;

    public ProblemShort() {
        
    }

    public ProblemShort(int problemId, String image, String description) {
        this.problemId = problemId;
        this.image = image;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
