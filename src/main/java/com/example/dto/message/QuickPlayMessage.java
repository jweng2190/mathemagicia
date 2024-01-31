package com.example.dto.message;

public class QuickPlayMessage implements Message {
    private String type;
    private String content;


    public QuickPlayMessage() {
    }

    public QuickPlayMessage(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
