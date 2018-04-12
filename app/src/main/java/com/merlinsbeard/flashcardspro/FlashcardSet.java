package com.merlinsbeard.flashcardspro;

public class FlashcardSet {
    private int setId;
    private String name;


    FlashcardSet(int setId, String name){
        this.name=name;
        this.setId=setId;
    }

    public int getSetId() {
        return setId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int setId) {
        this.setId = setId;
    }
}
