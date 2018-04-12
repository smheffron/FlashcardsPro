package com.merlinsbeard.flashcardspro;

public class FlashcardSet {
    private int userId;
    private String name;


    FlashcardSet(int userId, String name){
        this.name=name;
        this.userId=userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
